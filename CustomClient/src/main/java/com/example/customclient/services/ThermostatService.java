package com.example.customclient.services;

import com.example.*;
import com.example.customclient.rest.dto.GetThermostatStatusResponse;
import io.grpc.Deadline;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.customclient.utils.GrpcTypesUtils.fromGrpc;

@Service
public class ThermostatService {
    private static final Logger log = LoggerFactory.getLogger(ThermostatService.class);
    @GrpcClient("thermostat-service")
    private GrpcThermostatServiceGrpc.GrpcThermostatServiceBlockingStub grpcBlockingStub;
    @GrpcClient("thermostat-service")
    private GrpcThermostatServiceGrpc.GrpcThermostatServiceStub grpcStub;

    public GetThermostatStatusResponse getThermostatStatus(int id) {
        ThermostatIdMessage request = ThermostatIdMessage.newBuilder().setId(id).build();
        com.example.GetThermostatStatusResponse status = grpcBlockingStub.withDeadline(Deadline.after(5, TimeUnit.SECONDS)).getThermostatStatus(request);
        return fromGrpc(status);
    }

    public void monitor(int id) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        MonitorCurrentTemperatureRequest request = MonitorCurrentTemperatureRequest.newBuilder()
                .setThermostatId(ThermostatIdMessage.newBuilder().setId(id).build())
                .setDurationMs(5000)
                .build();

        grpcStub.monitorCurrentTemperature(request, new StreamObserver<>() {
            @Override
            public void onNext(MonitorCurrentTemperatureResponse response) {
                double curTemp = response.getCurrentTemperature();
                log.info("Current temperature: {}", curTemp);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                log.info("Thermostat monitor completed");
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
    }

    public boolean checkConnection() throws InterruptedException {
        final int countSent = 10;
        AtomicInteger countReceived = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<Empty> request = grpcStub.checkConnectionWithThermostat(new StreamObserver<>() {
            @Override
            public void onNext(CheckConnectionWithThermostatResponse response) {
                countReceived.set(response.getCountConnectionAttempts());
            }

            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            for (int i = 0; i < countSent; i++) {
                request.onNext(Empty.newBuilder().build());
                Thread.sleep(300);
            }
        } finally {
            request.onCompleted();
        }

        latch.await(5, TimeUnit.SECONDS);

        return countReceived.get() == countSent;
    }


    public List<Boolean> adjust(int id, List<Double> targetTemps) throws InterruptedException {
        List<Boolean> successes = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        var request = grpcStub.adjustTemperature(new StreamObserver<>() {
            @Override
            public void onNext(Empty empty) {
                successes.add(true);
            }

            @Override
            public void onError(Throwable throwable) {
                successes.add(false);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        try {
            for (Double targetTemp : targetTemps) {
                request.onNext(AdjustTemperatureRequest.newBuilder().setId(id).setTargetTemperature(targetTemp).build());
                Thread.sleep(300);
            }
        } finally {
            request.onCompleted();
        }

        latch.await(5, TimeUnit.SECONDS);
        return successes;
    }
}

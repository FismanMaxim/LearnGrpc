package com.example.server.grpc;

import com.example.*;
import com.example.server.exception.ModelNotFoundException;
import com.example.server.models.ThermostatStatus;
import com.example.server.services.ThermostatService;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import static com.example.server.utils.GrpcErrorUtils.internalError;
import static com.example.server.utils.GrpcTypesUtils.toGrpc;

@GrpcService
public class ThermostatGrpcAdapter extends GrpcThermostatServiceGrpc.GrpcThermostatServiceImplBase {
    private final ThermostatService thermostatService;

    public ThermostatGrpcAdapter(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

    @Override
    public void getThermostatStatus(
            ThermostatIdMessage request, StreamObserver<GetThermostatStatusResponse> responseObserver) {
        try {
            long id = request.getId();

            String name;
            ThermostatStatus status;
            name = thermostatService.getThermostatName(id);
            status = thermostatService.getThermostatStatus(id);

            var statusResponse = GetThermostatStatusResponse.newBuilder()
                    .setId(id)
                    .setName(name)
                    .setHeatingStatus(toGrpc(status.heatingStatus()))
                    .setTargetTemperature(status.targetTemp())
                    .setCurrentTemperature(status.currentTemp())
                    .build();
            responseObserver.onNext(statusResponse);
            responseObserver.onCompleted();
        } catch (ModelNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(internalError(e));
        }
    }

    @Override
    public void monitorCurrentTemperature(
            MonitorCurrentTemperatureRequest request,
            StreamObserver<MonitorCurrentTemperatureResponse> responseObserver) {
        try {
            long id = request.getThermostatId().getId();
            long durationMs = request.getDurationMs();
            long endTime = System.currentTimeMillis() + durationMs;

            while (System.currentTimeMillis() < endTime) {
                if (((ServerCallStreamObserver<?>) responseObserver).isCancelled()) {
                    break;
                }

                double currentTemp = thermostatService.getCurrentTemperature(id);

                responseObserver.onNext(
                        MonitorCurrentTemperatureResponse.newBuilder().setCurrentTemperature(currentTemp).build());

                Thread.sleep(500);
            }

            responseObserver.onCompleted();
        } catch (ModelNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).asRuntimeException());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Monitoring interrupted")
                    .withCause(e)
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error during monitoring")
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public StreamObserver<Empty> checkConnectionWithThermostat(
            StreamObserver<CheckConnectionWithThermostatResponse> responseObserver) {
        return new StreamObserver<>() {
            private int countConnections = 0;

            @Override
            public void onNext(Empty empty) {
                countConnections++;
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(CheckConnectionWithThermostatResponse.newBuilder()
                        .setCountConnectionAttempts(countConnections).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<AdjustTemperatureRequest> adjustTemperature(StreamObserver<Empty> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(AdjustTemperatureRequest adjustTemperatureRequest) {
                try {
                    long id = adjustTemperatureRequest.getId();
                    double targetTemperature = adjustTemperatureRequest.getTargetTemperature();

                    thermostatService.adjustTemperature(id, targetTemperature);
                    responseObserver.onNext(Empty.newBuilder().build());
                } catch (ModelNotFoundException e) {
                    responseObserver.onError(Status.NOT_FOUND.withCause(e).asRuntimeException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}

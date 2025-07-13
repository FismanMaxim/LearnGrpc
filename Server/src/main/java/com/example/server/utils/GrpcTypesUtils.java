package com.example.server.utils;

import com.example.server.models.ThermostatHeatingStatus;

public class GrpcTypesUtils {
    private GrpcTypesUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static com.example.ThermostatHeatingStatus toGrpc(ThermostatHeatingStatus status) {
        return switch (status) {
            case OFF -> com.example.ThermostatHeatingStatus.OFF;
            case HEATING -> com.example.ThermostatHeatingStatus.HEATING;
            case COOLING -> com.example.ThermostatHeatingStatus.COOLING;
        };
    }
}

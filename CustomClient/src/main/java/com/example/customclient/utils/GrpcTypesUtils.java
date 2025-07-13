package com.example.customclient.utils;

import com.example.customclient.rest.dto.GetThermostatStatusResponse;
import com.example.customclient.rest.dto.ThermostatHeatingStatus;

public class GrpcTypesUtils {
    private GrpcTypesUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static GetThermostatStatusResponse fromGrpc(com.example.GetThermostatStatusResponse response) {
        return new GetThermostatStatusResponse(
                response.getName(),
                response.getCurrentTemperature(),
                response.getTargetTemperature(),
                fromGrpc(response.getHeatingStatus()));
    }

    public static ThermostatHeatingStatus fromGrpc(com.example.ThermostatHeatingStatus status) {
        return switch (status) {
            case OFF -> ThermostatHeatingStatus.OFF;
            case COOLING -> ThermostatHeatingStatus.COOLING;
            case HEATING -> ThermostatHeatingStatus.HEATING;
            case UNRECOGNIZED -> null;
        };
    }
}

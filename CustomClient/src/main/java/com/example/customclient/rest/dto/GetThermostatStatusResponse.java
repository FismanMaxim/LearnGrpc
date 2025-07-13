package com.example.customclient.rest.dto;

public record GetThermostatStatusResponse(
        String name,
        double currentTemperature,
        double targetTemperature,
        ThermostatHeatingStatus heatingStatus) {
}

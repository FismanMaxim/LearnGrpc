package com.example.server.models;

public record ThermostatStatus(ThermostatHeatingStatus heatingStatus, double currentTemp, double targetTemp) {
}

package com.example.customclient.rest.dto;

import java.util.List;

public record AdjustThermostatRequest(List<Double> targetTemps) {
}

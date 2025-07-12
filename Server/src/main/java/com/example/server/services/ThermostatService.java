package com.example.server.services;

import com.example.server.exception.ModelNotFoundException;
import com.example.server.models.Thermostat;
import com.example.server.models.ThermostatStatus;
import com.example.server.repository.ThermostatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThermostatService {
    private static final Logger log = LoggerFactory.getLogger(ThermostatService.class);
    private final ThermostatRepository thermostatRepository;

    public ThermostatService(ThermostatRepository thermostatRepository) {
        this.thermostatRepository = thermostatRepository;
    }

    public Thermostat requireById(long thermostatId) {
        Thermostat thermostat = thermostatRepository.get(thermostatId);
        if (thermostat == null) {
            throw new ModelNotFoundException(Thermostat.class, thermostatId);
        }
        return thermostat;
    }

    public ThermostatStatus getThermostatStatus(long thermostatId) {
        return requireById(thermostatId).getStatus();
    }

    public String getThermostatName(long id) {
        return requireById(id).getName();
    }

    public void adjustTemperature(long id, double targetTemperature) {
        Thermostat thermostat = requireById(id);
        ThermostatStatus cur = thermostat.getStatus();

        thermostat.setStatus(new ThermostatStatus(cur.heatingStatus(), cur.currentTemp(), targetTemperature));
        log.info("Target temperature changed: thermostat_id={}, targetTemperature={}", id, targetTemperature);
    }

    public double getCurrentTemperature(long id) {
        return requireById(id).getStatus().currentTemp();
    }
}

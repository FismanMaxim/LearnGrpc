package com.example.server.repository;

import com.example.server.models.Thermostat;
import com.example.server.models.ThermostatHeatingStatus;
import com.example.server.models.ThermostatStatus;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ThermostatRepository {
    private final Map<Long, Thermostat> thermostats = new HashMap<>();

    public ThermostatRepository() {
        for (int i = 0; i < 10; i++) {
            thermostats.put((long) i, new Thermostat(i, "thermostat#" + i,
                    new ThermostatStatus(ThermostatHeatingStatus.HEATING, 0, 0)));
        }
    }

    public Thermostat get(long id) {
        return thermostats.get(id);
    }
}

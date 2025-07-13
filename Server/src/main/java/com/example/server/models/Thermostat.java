package com.example.server.models;

public class Thermostat {
    private final long id;
    private final String name;
    private ThermostatStatus status;

    public Thermostat(long id, String name, ThermostatStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ThermostatStatus getStatus() {
        return status;
    }

    public void setStatus(ThermostatStatus status) {
        this.status = status;
    }
}

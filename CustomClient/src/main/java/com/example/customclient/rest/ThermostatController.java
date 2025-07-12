package com.example.customclient.rest;

import com.example.customclient.rest.dto.AdjustThermostatRequest;
import com.example.customclient.rest.dto.AdjustThermostatResponse;
import com.example.customclient.rest.dto.GetThermostatStatusResponse;
import com.example.customclient.services.ThermostatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ThermostatController {
    private final ThermostatService thermostatService;

    public ThermostatController(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

    @GetMapping("/api/thermostat/{id}/status")
    public GetThermostatStatusResponse getThermostat(@PathVariable int id) {
        return thermostatService.getThermostatStatus(id);
    }

    @PostMapping("/api/thermostat/check-connection")
    public boolean checkConnection() throws InterruptedException {
        return thermostatService.checkConnection();
    }

    @GetMapping("api/thermostat/{id}/monitor")
    public void getMonitor(@PathVariable int id) throws InterruptedException {
        thermostatService.monitor(id);
    }

    // In order to work with bidirectional stream communication, let's introduce this endpoint:
    // It takes a list of targetTemperatures and then sends request for these temperatures with small pauses
    @PostMapping("api/thermostat/{id}/adjust")
    public ResponseEntity<AdjustThermostatResponse> adjustThermostat(@PathVariable int id, @RequestBody AdjustThermostatRequest request) throws InterruptedException {
        if (request == null || request.targetTemps() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Boolean> successes = thermostatService.adjust(id, request.targetTemps());
        AdjustThermostatResponse response = new AdjustThermostatResponse(successes);
        return ResponseEntity.ok(response);
    }
}

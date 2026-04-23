package com.smartcampus.service;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SensorReadingService {

    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
    private final SensorService sensorService = new SensorService();

    public List<SensorReading> getReadings(String sensorId) {
        List<SensorReading> sensorReadings = readings.get(sensorId);
        if (sensorReadings == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(sensorReadings);
    }

    public SensorReading addReading(String sensorId, SensorReading reading) {
        Sensor sensor = sensorService.getSensor(sensorId);

        if (sensor != null && "MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in maintenance and cannot accept readings.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        readings.computeIfAbsent(sensorId, key -> new ArrayList<>()).add(reading);
        sensorService.updateCurrentValue(sensorId, reading.getValue());
        return reading;
    }
}

package com.smartcampus.service;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorService {

    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final RoomService roomService = new RoomService();

    public Collection<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }

    public List<Sensor> getSensorsByType(String type) {
        List<Sensor> matchingSensors = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (sensor.getType() != null && sensor.getType().equalsIgnoreCase(type)) {
                matchingSensors.add(sensor);
            }
        }
        return matchingSensors;
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public Sensor addSensor(Sensor sensor) {
        if (!roomService.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist.");
        }

        sensors.put(sensor.getId(), sensor);
        roomService.addSensorToRoom(sensor.getRoomId(), sensor.getId());
        return sensor;
    }

    public void updateCurrentValue(String sensorId, double currentValue) {
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(currentValue);
        }
    }
}

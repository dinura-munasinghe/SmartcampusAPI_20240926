package com.smartcampus.service;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomService {

    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Collection<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public Room addRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    public boolean deleteRoom(String id) {
        Room room = rooms.get(id);
        if (room == null) {
            return false;
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + id + " cannot be deleted because it still has sensors assigned.");
        }

        rooms.remove(id);
        return true;
    }

    public void addSensorToRoom(String roomId, String sensorId) {
        Room room = rooms.get(roomId);
        if (room != null && !room.getSensorIds().contains(sensorId)) {
            room.getSensorIds().add(sensorId);
        }
    }
}

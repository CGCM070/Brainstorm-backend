package org.brainstorm.service;

import org.brainstorm.model.Rooms;
import org.brainstorm.repository.RoomsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {


    @Autowired
    private RoomsInterface roomsInterface;

    public List<Rooms> getAllRooms() {
        return roomsInterface.findAll();
    }
    public Rooms getRoomByCode(String code) {
        return roomsInterface.findByCode(code).orElse(null);
    }

}


package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Rooms;
import org.brainstorm.repository.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {


    @Autowired
    private RoomsRepository roomsRepository;

    public List<Rooms> getAllRooms() {
        return roomsRepository.findAll();
    }
    public Rooms getRoomByCode(String code) {
        return roomsRepository.findByCode(code).orElse(null);
    }


    @Transactional
    public Rooms createRoom ( Rooms rooms){
        if (roomsRepository.findByCode(rooms.getCode()).isPresent()) {
            throw  new RuntimeException( "Sala ya registrada");
        }
        return roomsRepository.save(rooms);
    }

    @Transactional
    public void deleteRoom (Long id) {
        Rooms room = roomsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException( " no se ha encontrado sala con id :  " + id));

        roomsRepository.delete(room);
    }

}


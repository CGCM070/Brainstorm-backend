package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.repository.RoomsRepository;
import org.brainstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {


    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Rooms> getAllRooms() {
        return roomsRepository.findAll();
    }

    public Rooms getRoomByCode(String code) {
        return roomsRepository.findByCode(code).orElse(null);
    }


    @Transactional
    public Rooms save(Rooms rooms) {
        return roomsRepository.save(rooms);
    }

    @Transactional
    public Rooms createRoomWithUser(Long userId, Rooms room) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));
        room.setCreatedBy(user.getUsername());
        room.getUsers().add(user);
        user.setRoom(room);
        return roomsRepository.save(room);
    }

    @Transactional
    public Rooms joinRoom(String code, Long userId) {
        Rooms room = roomsRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada con código: " + code));
        if (room.getUsers().size() >= room.getMaxUsers()) {
            throw new IllegalArgumentException("La sala está llena");
        }
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));
        room.getUsers().add(user);
        user.setRoom(room);
        return roomsRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Rooms room = roomsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(" no se ha encontrado sala con id :  " + id));

        roomsRepository.delete(room);
    }

}


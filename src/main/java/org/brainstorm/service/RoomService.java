package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.repository.RoomsRepository;
import org.brainstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        room.setCreatedAt(LocalDateTime.now());
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

        // Verificar si el usuario ya está en la sala
        boolean userAlreadyInRoom = room.getUsers().stream()
                .anyMatch(existingUser -> existingUser.getId().equals(userId));

        if (userAlreadyInRoom) {
            throw new RuntimeException("El usuario ya está en esta sala");
        }

        // Verificar si ya existe un usuario con el mismo nombre en la sala
        boolean usernameExists = room.getUsers().stream()
                .anyMatch(existingUser -> existingUser.getUsername().equalsIgnoreCase(user.getUsername()));

        if (usernameExists) {
            throw new RuntimeException("Ya existe un usuario con ese nombre en la sala");
        }

        // Remover al usuario de su sala anterior si está en una
        if (user.getRoom() != null) {
            Rooms previousRoom = user.getRoom();
            previousRoom.getUsers().remove(user);
            roomsRepository.save(previousRoom);
        }

        // Agregar usuario a la nueva sala
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

    @Transactional
    public void removeUserFromRoom(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));

        Rooms room = user.getRoom();
        if (room != null) {
            room.getUsers().remove(user);
            user.setRoom(null);
            roomsRepository.save(room);
            userRepository.save(user);
        }
//        userRepository.delete(user);
    }
}

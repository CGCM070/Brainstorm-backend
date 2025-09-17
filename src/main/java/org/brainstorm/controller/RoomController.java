package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.model.Rooms;
import org.brainstorm.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("")
    public ResponseEntity<List<Rooms>> getAllRooms() {
        List<Rooms> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }


    @GetMapping("/{code}")
    public ResponseEntity<Rooms> getRoomByCode(@PathVariable String code) {
        Rooms room = roomService.getRoomByCode(code);
        if (room == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(room);
    }

    @PostMapping("")
    public ResponseEntity<Rooms> createRoom (@RequestBody @Valid Rooms rooms) {
        Rooms createRoom = roomService.save(rooms);
        return new ResponseEntity<>(createRoom, HttpStatus.CREATED);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Rooms> createRoomWithUser(
            @PathVariable Long userId,
            @RequestBody @Valid Rooms room) {
        Rooms created = roomService.createRoomWithUser(userId, room);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/join/{code}/user/{userId}")
    public ResponseEntity<Rooms> joinRoom(
            @PathVariable String code,
            @PathVariable Long userId) {
        Rooms joined = roomService.joinRoom(code, userId);
        return ResponseEntity.ok(joined);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom (@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> removeUserFromRoom( @PathVariable Long userId) {
        roomService.removeUserFromRoom( userId);
        return ResponseEntity.ok().build();
    }

}

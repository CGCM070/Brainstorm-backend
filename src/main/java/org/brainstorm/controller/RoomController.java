package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.model.Rooms;
import org.brainstorm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;


    @GetMapping("/all")
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
        Rooms createRoom = roomService.createRoom(rooms);
        return new ResponseEntity<>(createRoom, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom (@PathVariable Long id) {
        roomService.deleteRoom(id);
    }


}

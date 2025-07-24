package org.brainstorm.controller;

import org.brainstorm.model.Rooms;
import org.brainstorm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private  RoomService roomService;


    /**
     * Obtiene todas las salas disponibles.
     *
     * @return Lista de salas.
     */
    @GetMapping("/all")
    public List<Rooms> getAllRooms() {
        return roomService.getAllRooms();
    }
    /**
     * Obtiene una sala por su código.
     *
     * @param code Código de la sala.
     * @return Sala correspondiente al código, o null si no existe.
     */
    @GetMapping("/{code}")
    public Rooms getRoomByCode(@PathVariable String code) {
        return roomService.getRoomByCode(code);
    }


}

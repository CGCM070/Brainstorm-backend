package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.model.Users;
import org.brainstorm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/users")

public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("all")
    public ResponseEntity<List<Users>> getAllRooms() {
        List<Users> usersList = userService.getAll();
        return ResponseEntity.ok(usersList);
    }

    @PostMapping("")
    public ResponseEntity<Users>createUser(@RequestBody @Valid Users user){
        Users createdUser = userService.create(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUsername
            (@PathVariable Long id, @RequestBody @Valid Users user) {
        Users updatedUser = userService.updateUsername(id, user);
        return ResponseEntity.ok(updatedUser);
    }


}

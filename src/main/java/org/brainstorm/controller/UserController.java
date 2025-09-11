package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.config.RequiresAuth;
import org.brainstorm.dto.UserResponseDto;
import org.brainstorm.model.Users;
import org.brainstorm.service.SessionTokenService;
import org.brainstorm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/users")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SessionTokenService sessionTokenService;

    @GetMapping("")
    public ResponseEntity<List<Users>> getAllRooms() {
        List<Users> usersList = userService.getAll();
        return ResponseEntity.ok(usersList);
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDto>createUser(@RequestBody @Valid Users user){
        Users createdUser = userService.create(user);
        String token = sessionTokenService.generateTokenForUser(createdUser.getId());
        UserResponseDto responseDto = new UserResponseDto(createdUser, token);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    @RequiresAuth
    public ResponseEntity<Users> updateUsername
            (@PathVariable Long id, @RequestBody @Valid Users user) {
        Users updatedUser = userService.updateUsername(id, user);
        return ResponseEntity.ok(updatedUser);
    }


}

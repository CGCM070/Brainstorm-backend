package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.model.Users;
import org.brainstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Users> getAll () {
        return userRepository.findAll();
    }

    @Transactional
    public Users create (Users users) {
        if (userRepository.findByUsernameAndRoom(users.getUsername(), users.getRoom()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese nombre en la sala");
        }
        return userRepository.save(users);
    }

    public Users updateUsername (Long id, Users users) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        existingUser.setUsername(users.getUsername());
        return userRepository.save(existingUser);
    }




}

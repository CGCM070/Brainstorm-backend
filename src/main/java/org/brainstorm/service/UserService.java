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
        users.setOnline(true);
        return userRepository.save(users);
    }

    @Transactional
    public Users updateUsername (Long id, Users users) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existingUser.setUsername(users.getUsername());
        existingUser.setRoom(null);
        return userRepository.save(existingUser);
    }




}

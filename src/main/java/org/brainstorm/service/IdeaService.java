package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.repository.IdeaRepository;
import org.brainstorm.repository.RoomsRepository;
import org.brainstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;
    @Autowired
    private RoomsRepository roomsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebSocketService webSocketService;

    public Page<Ideas> getAllIdeas(Pageable pageable) {
        return ideaRepository.findAll(pageable);
    }

    @Transactional
    public Ideas createIdea(Ideas idea, Long  userId, Long roomId) {
        Rooms room = roomsRepository.findById(roomId).
                orElseThrow(() -> new EntityNotFoundException("Sala no encontrada con id : " + roomId));

        Users user = userRepository.findById(userId).orElseThrow(
                () ->  new EntityNotFoundException("Usuario no econtrado con id : " + userId)
        );


        boolean userExist = room.getUsers()
                .stream()
                .anyMatch(u -> u.getUsername()
                        .equals(user.getUsername()));

        if (!userExist) {
            throw new IllegalArgumentException("El autor no pertence a la sala especificada");
        }


        idea.setAuthor(user.getUsername());
        idea.setRoom(room);
        idea.setUser(user);
        room.getIdeas().add(idea);
        Ideas savedIdea = ideaRepository.save(idea);

        // Notificar via WebSocket
        webSocketService.notifyIdeaCreated(room.getCode(), savedIdea, user.getUsername());

        return savedIdea;
    }

    @Transactional
    public Ideas updateIdea(Long id, Long userId, Ideas idea) {
        Ideas existingIdea = ideaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + id));

        Users user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("No se ha encotrado el usuario con id : " + userId)
        );

        if (!existingIdea.getAuthor().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("No puedes actualizar una idea que no te pertenece");
        }

        existingIdea.setTitle(idea.getTitle());
        existingIdea.setDescription(idea.getDescription());
        existingIdea.setUpdatedAt(Instant.now());
        Ideas updatedIdea = ideaRepository.save(existingIdea);

        // Notificar via WebSocket
        webSocketService.notifyIdeaUpdated(existingIdea.getRoom().getCode(), id, updatedIdea, user.getUsername());

        return updatedIdea;
    }

    @Transactional
    public void deleteIdea(Long id, Long userId) {
        Ideas existingIdea = ideaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + id));

        Users user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("No se ha encotrado el usuario con id : " + userId)
        );

        if (!existingIdea.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No puedes eliminar una idea que no te pertenece");
        }

        String roomCode = existingIdea.getRoom().getCode();
        ideaRepository.deleteById(id);

        // Notificar via WebSocket
        webSocketService.notifyIdeaDeleted(roomCode, id, user.getUsername());
    }

}

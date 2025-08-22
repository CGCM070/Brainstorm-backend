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
    public Ideas createIdea(Ideas idea , String username, Long roomId ) {
        Rooms room = roomsRepository.findById(roomId).
                orElseThrow(() -> new EntityNotFoundException("Sala no encontrada con id : " + roomId));

        boolean userExist = room.getUsers()
                .stream()
                .anyMatch(users -> users.getUsername()
                        .equals(username));

        if (!userExist) {
            throw new IllegalArgumentException ("El autor no pertence a la sala especificada");
        }
        idea.setAuthor(username);
        idea.setRoom(room);
        room.getIdeas().add(idea);
        Ideas savedIdea = ideaRepository.save(idea);

        // Notificar via WebSocket
        webSocketService.notifyIdeaCreated(room.getCode(), savedIdea, username);

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
            throw  new IllegalArgumentException("No puedes actualizar una idea que no te pertenece");
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

        if (!existingIdea.getAuthor().equalsIgnoreCase(user.getUsername())) {
            throw  new IllegalArgumentException("No puedes eliminar una idea que no te pertenece");
        }

        String roomCode = existingIdea.getRoom().getCode();
        ideaRepository.deleteById(id);

        // Notificar via WebSocket
        webSocketService.notifyIdeaDeleted(roomCode, id, user.getUsername());
    }

    @Transactional
    public Ideas votarIdea(Long ideaId, String username, int value){

        if (value != 1 && value != -1 && value != 0) {
            throw new IllegalArgumentException("El valor de voto debe ser 1, -1 o 0");
        }

        Ideas idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + ideaId));

        Integer previousVote = idea.getUserVotes().get(username);
        if (previousVote != null) {
            // Resta el voto anterior
            idea.setTotalVotes(idea.getTotalVotes() - previousVote);
        }

        if (value == 0) {
            // Eliminar el voto
            idea.getUserVotes().remove(username);
        } else {
            // Suma el nuevo voto
            idea.setTotalVotes(idea.getTotalVotes() + value);
            idea.getUserVotes().put(username, value);
        }

        Ideas updatedIdea = ideaRepository.save(idea);

        // Notificar via WebSocket
        webSocketService.notifyIdeaVoted(idea.getRoom().getCode(), ideaId, updatedIdea, username);

        return updatedIdea;
    }
}

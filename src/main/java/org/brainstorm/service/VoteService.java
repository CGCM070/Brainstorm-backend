package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.dto.IdeaVoteResponseDto;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.model.Votes;
import org.brainstorm.repository.IdeaRepository;
import org.brainstorm.repository.UserRepository;
import org.brainstorm.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Transactional
    public IdeaVoteResponseDto votarIdea(Long ideaId, Long userId, int value) {
        // Validaciones tempranas
        validateVoteValue(value);

        Ideas idea = findIdeaById(ideaId);
        Users user = findUserById(userId);
        validateUserInRoom(user, idea.getRoom());

        // Procesar voto
        processVote(idea, user, value);

        Ideas updatedIdea = ideaRepository.save(idea);

        // Notificar via WebSocket
        webSocketService.notifyIdeaVoted(idea.getRoom().getCode(), ideaId, updatedIdea, user.getUsername());

        return new IdeaVoteResponseDto(updatedIdea.getId(), updatedIdea.getTitle(), updatedIdea.getTotalVotes());
    }

    private void validateVoteValue(int value) {
        if (value != 1 && value != -1 && value != 0) {
            throw new IllegalArgumentException("El valor de voto debe ser 1, -1 o 0");
        }
    }

    private Ideas findIdeaById(Long ideaId) {
        return ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + ideaId));
    }

    private Users findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + userId));
    }

    private void validateUserInRoom(Users user, Rooms room) {
        boolean userInRoom = room.getUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!userInRoom) {
            throw new IllegalArgumentException("El usuario no pertenece a la sala de la idea");
        }
    }

    private void processVote(Ideas idea, Users user, int value) {
        Optional<Votes> existingVote = voteRepository.findByIdeaAndUser(idea, user);

        if (existingVote.isPresent()) {
            updateExistingVote(idea, existingVote.get(), value);
        } else {
            createNewVote(idea, user, value);
        }
    }

    private void updateExistingVote(Ideas idea, Votes existingVote, int newValue) {
        int previousValue = existingVote.getVoteValue();
        idea.setTotalVotes(idea.getTotalVotes() - previousValue);

        if (newValue == 0) {
            voteRepository.delete(existingVote);
            idea.getVotes().remove(existingVote);
            existingVote.getUser().getVotes().remove(existingVote);
        } else {
            existingVote.setVoteValue(newValue);
            existingVote.setUpdatedAt(Instant.now());
            voteRepository.save(existingVote);
            idea.setTotalVotes(idea.getTotalVotes() + newValue);
        }
    }

    private void createNewVote(Ideas idea, Users user, int value) {
        if (value == 0) {
            return; // No crear voto con valor 0
        }

        Votes newVote = Votes.builder()
                .idea(idea)
                .user(user)
                .voteValue(value)
                .createdAt(Instant.now())
                .build();

        voteRepository.save(newVote);
        idea.setTotalVotes(idea.getTotalVotes() + value);


        idea.getVotes().add(newVote);
        user.getVotes().add(newVote);
    }
}
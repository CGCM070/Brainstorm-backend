package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Rooms;
import org.brainstorm.repository.IdeaRepository;
import org.brainstorm.repository.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;
    @Autowired
    private RoomsRepository roomsRepository;

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
        return ideaRepository.save(idea);
    }

    @Transactional
    public Ideas updateIdea(Long id, Ideas idea) {
        Ideas existingIdea = ideaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + id));
        existingIdea.setTitle(idea.getTitle());
        existingIdea.setDescription(idea.getDescription());
        existingIdea.setUpdatedAt(LocalDateTime.now());
        return ideaRepository.save(existingIdea);
    }

    @Transactional
    public void deleteIdea(Long id) {
        if (!ideaRepository.existsById(id)) {
            throw new EntityNotFoundException("Idea not found with id: " + id);
        }
        ideaRepository.deleteById(id);
    }

    @Transactional
    public void votarIdea(Long id, String username, int value){

        if (value != 1 && value != -1) {
            throw new IllegalArgumentException("El valor de voto debe ser 1 o -1");
        }

        Ideas idea = ideaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found with id: " + id));
        Integer previousVote = idea.getUserVotes().get(username);
        if (previousVote != null) {
            // Resta el voto anterior
            idea.setTotalVotes(idea.getTotalVotes() - previousVote);
        }
        // Suma el nuevo voto
        idea.setTotalVotes(idea.getTotalVotes() + value);
        idea.getUserVotes().put(username, value);
        ideaRepository.save(idea);
    }
}

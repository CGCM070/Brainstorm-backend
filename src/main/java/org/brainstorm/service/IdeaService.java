package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Ideas;
import org.brainstorm.repository.IdeaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    public Page<Ideas> getAllIdeas(Pageable pageable) {
        return ideaRepository.findAll(pageable);
    }

    @Transactional
    public Ideas createIdea(Ideas idea) {
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

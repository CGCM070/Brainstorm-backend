package org.brainstorm.service;

import jakarta.transaction.Transactional;
import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Comments;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Users;
import org.brainstorm.repository.CommentRepository;
import org.brainstorm.repository.IdeaRepository;
import org.brainstorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private IdeaRepository ideaRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebSocketService webSocketService;

    public List<Comments> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comments> getCommentsByIdeaId(Long ideaId) {
        return commentRepository.findByIdeaId(ideaId);
    }


    @Transactional
    public Comments createCommentOnIdea(Long ideaID, Long userID, Comments comment) {
        Ideas idea = ideaRepository.findById(ideaID).orElseThrow(
                () -> new EntityNotFoundException("No se ha encontrado la idea con id : " + ideaID)
        );
        Users user = userRepository.findById(userID).orElseThrow(
                () -> new EntityNotFoundException("No se ha encontrado el usuario con id : " + userID)
        );

        Long ideaRoomId = idea.getRoom().getId();
        Long userRoomId = user.getRoom().getId();
        if (!ideaRoomId.equals(userRoomId)) {
            throw new IllegalArgumentException("El usuario no pertenece a la sala de la idea");
        }

        comment.setAuthorUsername(user.getUsername());
        comment.setIdea(idea);
        idea.getComments().add(comment);
        Comments savedComment = commentRepository.save(comment);

        // Notificar via WebSocket
        webSocketService.notifyCommentCreated(idea.getRoom().getCode(), ideaID, savedComment, user.getUsername());

        return savedComment;
    }

    @Transactional
    public Comments updateComment(Long id,Long userId, Comments comment) {
        Comments existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        Users user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("No se ha encontrado el usuario con id : " + userId)
        );
        if (!existingComment.getAuthorUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("No puedes editar un comentario que no te pertence");
        }
        existingComment.setContent(comment.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());
        Comments updatedComment = commentRepository.save(existingComment);

        // Notificar via WebSocket
        webSocketService.notifyCommentUpdated(
            existingComment.getIdea().getRoom().getCode(),
            existingComment.getIdea().getId(),
            updatedComment,
            user.getUsername()
        );

        return updatedComment;
    }

    @Transactional
    public void deleteComment(Long id, Long userID) {
        Comments existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        Users user = userRepository.findById(userID).orElseThrow(
                () -> new EntityNotFoundException("No se ha encontrado el usuario con id : " + userID)
        );
        if (!existingComment.getAuthorUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("No puedes borrar un comentario que no te pertence");
        }

        String roomCode = existingComment.getIdea().getRoom().getCode();
        Long ideaId = existingComment.getIdea().getId();

        commentRepository.deleteById(id);

        // Notificar via WebSocket
        webSocketService.notifyCommentDeleted(roomCode, ideaId, id, user.getUsername());
    }
}

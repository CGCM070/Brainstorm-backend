package org.brainstorm.service;

import org.brainstorm.exception.EntityNotFoundException;
import org.brainstorm.model.Comments;
import org.brainstorm.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comments> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comments> getCommentsByIdeaId(Long ideaId) {
        return commentRepository.findByIdeaId(ideaId);
    }

    public Comments createComment(Comments comment) {
        return commentRepository.save(comment);
    }

    public Comments updateComment(Long id, Comments comment) {
        Comments existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));

        existingComment.setContent(comment.getContent());
        existingComment.setUpdatedAt(comment.getUpdatedAt());
        return commentRepository.save(existingComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}


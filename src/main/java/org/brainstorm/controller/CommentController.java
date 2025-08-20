package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.model.Comments;
import org.brainstorm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("")
    public ResponseEntity<List<Comments>> getAllComments() {
        List<Comments> comments = commentService.getAllComments();
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/idea/{ideaId}")
    public ResponseEntity<List<Comments>> getCommentsByIdeaId(@PathVariable Long ideaId) {
        List<Comments> comments = commentService.getCommentsByIdeaId(ideaId);
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/user/{userId}/idea/{ideaId}")
    public ResponseEntity<Comments> createCommentOnIdea( @PathVariable Long ideaId ,
                                                         @PathVariable Long userId ,
                                                         @RequestBody @Valid  Comments comment) {
        Comments createdComment = commentService.createCommentOnIdea(ideaId,userId,comment);
        return ResponseEntity.status (201).body(createdComment);
    }

    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Comments> updateComment(@PathVariable Long userId, @PathVariable Long id, @RequestBody  @Valid  Comments comment) {
        Comments updatedComment = commentService.updateComment(id, userId, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @PathVariable Long userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }
}
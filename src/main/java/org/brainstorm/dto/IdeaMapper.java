package org.brainstorm.dto;

import org.brainstorm.model.Ideas;
import org.brainstorm.model.Comments;

public class IdeaMapper {

    public static IdeaWebSocketDto toWebSocketDto(Ideas idea) {
        return IdeaWebSocketDto.builder()
                .id(idea.getId())
                .title(idea.getTitle())
                .description(idea.getDescription())
                .author(idea.getAuthor())
                .userId(idea.getUser() != null ? idea.getUser().getId() : null)
                .totalVotes(idea.getTotalVotes())
                .createdAt(idea.getCreatedAt())
                .updatedAt(idea.getUpdatedAt())
                .roomId(idea.getRoom() != null ? idea.getRoom().getId() : null)
                .roomCode(idea.getRoom() != null ? idea.getRoom().getCode() : null)
                .build();
    }

    public static CommentWebSocketDto toWebSocketDto(Comments comment) {
        return CommentWebSocketDto.builder()
                .id(comment.getId())
                .authorUsername(comment.getAuthorUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .ideaId(comment.getIdea() != null ? comment.getIdea().getId() : null)
                .build();
    }
}

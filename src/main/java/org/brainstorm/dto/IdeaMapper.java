package org.brainstorm.dto;

import org.brainstorm.model.Ideas;

public class IdeaMapper {

    public static IdeaWebSocketDto toWebSocketDto(Ideas idea) {
        return IdeaWebSocketDto.builder()
                .id(idea.getId())
                .title(idea.getTitle())
                .description(idea.getDescription())
                .author(idea.getAuthor())
                .totalVotes(idea.getTotalVotes())
                .createdAt(idea.getCreatedAt())
                .updatedAt(idea.getUpdatedAt())
                .roomId(idea.getRoom() != null ? idea.getRoom().getId() : null)
                .roomCode(idea.getRoom() != null ? idea.getRoom().getCode() : null)
                .build();
    }
}

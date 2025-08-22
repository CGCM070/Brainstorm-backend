package org.brainstorm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class IdeaWebSocketDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private Integer totalVotes;
    private Instant createdAt;
    private Instant updatedAt;
    private Long roomId;
    private String roomCode;
}

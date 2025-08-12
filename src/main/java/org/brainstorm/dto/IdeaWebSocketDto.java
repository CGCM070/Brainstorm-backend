package org.brainstorm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class IdeaWebSocketDto {
    private Long id;
    private String title;
    private String description;
    private String author;
    private Integer totalVotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long roomId;
    private String roomCode;
}

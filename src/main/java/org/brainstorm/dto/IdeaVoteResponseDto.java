package org.brainstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaVoteResponseDto {
    private Long id;
    private String title;
    private Integer totalVotes;
}

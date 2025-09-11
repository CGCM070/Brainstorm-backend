package org.brainstorm.controller;

import org.brainstorm.dto.IdeaVoteResponseDto;
import org.brainstorm.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/idea/{ideaId}/user/{userId}")
    public ResponseEntity<IdeaVoteResponseDto> votarIdea(
            @PathVariable Long ideaId,
            @PathVariable Long userId,
            @RequestParam int value) {
        IdeaVoteResponseDto result = voteService.votarIdea(ideaId, userId, value);
        return ResponseEntity.ok(result);
    }
}
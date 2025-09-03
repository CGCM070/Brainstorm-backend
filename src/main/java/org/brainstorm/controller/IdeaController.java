package org.brainstorm.controller;

import jakarta.validation.Valid;
import org.brainstorm.model.Ideas;
import org.brainstorm.service.IdeaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/ideas")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("")
    public Page<Ideas> getAllIdeas(Pageable pageable) {
        return ideaService.getAllIdeas(pageable);
    }

    @PostMapping("/rooms/{roomId}")
    public ResponseEntity< Ideas> createIdea(@RequestBody @Valid Ideas idea,
                                           @RequestParam Long userId,
                                           @PathVariable Long roomId) {
        Ideas createdIdea = ideaService.createIdea(idea,userId ,roomId);
        return  new ResponseEntity<>(createdIdea, HttpStatus.CREATED);
    }


    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Ideas> updateIdea(@PathVariable Long id,
                                            @PathVariable Long userId,
                                            @RequestBody @Valid Ideas idea) {
        Ideas updatedIdea = ideaService.updateIdea(id, userId,idea);
        return new ResponseEntity<>(updatedIdea, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteIdea(@PathVariable Long id, @PathVariable Long userId) {
        ideaService.deleteIdea(id, userId);
        return ResponseEntity.ok().build();
    }


}

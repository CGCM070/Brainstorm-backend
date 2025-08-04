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

    @GetMapping("/all")
    public Page<Ideas> getAllIdeas(Pageable pageable) {
        return ideaService.getAllIdeas(pageable);
    }

    @PostMapping("")
    public ResponseEntity< Ideas> createIdea( @RequestBody @Valid Ideas idea) {
        Ideas createdIdea = ideaService.createIdea(idea);
        return  new ResponseEntity<>(createdIdea, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<Void> votarIdea(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam int value) {
        ideaService.votarIdea(id, username, value);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Ideas> updateIdea(@PathVariable Long id, @RequestBody @Valid Ideas idea) {
        Ideas updatedIdea = ideaService.updateIdea(id, idea);
        return new ResponseEntity<>(updatedIdea, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteIdea(@PathVariable Long id) {
        ideaService.deleteIdea(id);
        ResponseEntity.status(HttpStatus.OK);
    }


}

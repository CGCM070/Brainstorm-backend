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

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/ideas")
public class IdeaController {

    @Autowired
    private IdeaService ideaService;

    @GetMapping("/all")
    public Page<Ideas> getAllIdeas(Pageable pageable) {
        return ideaService.getAllIdeas(pageable);
    }

    @PostMapping("/rooms/{roomId}")
    public ResponseEntity< Ideas> createIdea(@RequestBody @Valid Ideas idea,
                                           @RequestParam String authorUsername,
                                           @PathVariable Long roomId) {
        Ideas createdIdea = ideaService.createIdea(idea,authorUsername,roomId);
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


    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<Ideas> updateIdea(@PathVariable Long id,
                                            @PathVariable Long userId,
                                            @RequestBody @Valid Ideas idea) {
        Ideas updatedIdea = ideaService.updateIdea(id, userId,idea);
        return new ResponseEntity<>(updatedIdea, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/user/{userId}")
    public void deleteIdea(@PathVariable Long id, @PathVariable Long userId) {
        ideaService.deleteIdea(id, userId);
        ResponseEntity.status(HttpStatus.OK);
    }


}

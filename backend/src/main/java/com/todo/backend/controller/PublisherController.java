package com.todo.backend.controller;

import com.todo.backend.entity.Publisher;
import com.todo.backend.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/publisher")
public class PublisherController {
    private PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity<?> createPublisher(@Valid @RequestBody Publisher publisher, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Publisher createdPublisher = publisherService.createPublisher(publisher);
            return ResponseEntity.ok(createdPublisher);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating publisher: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePublisher(@PathVariable String id, @Valid @RequestBody Publisher publisher, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Publisher updatedPublisher = publisherService.updatePublisher(publisher);
            return ResponseEntity.ok(updatedPublisher);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating publisher: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublisher(@PathVariable String id) {
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.ok("Publisher deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting publisher: " + e.getMessage());
        }
    }
}

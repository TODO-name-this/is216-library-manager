package com.todo.backend.controller;

import com.todo.backend.dto.publisher.PublisherDto;
import com.todo.backend.dto.publisher.ResponsePublisherDto;
import com.todo.backend.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/publisher")
public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllPublishers() {
        try {
            return ResponseEntity.ok(publisherService.getAllPublishers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching publishers: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPublisher(@PathVariable String id) {
        try {
            ResponsePublisherDto publisher = publisherService.getPublisher(id);
            return ResponseEntity.ok(publisher);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping
    public ResponseEntity<?> createPublisher(@Valid @RequestBody PublisherDto publisherDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponsePublisherDto createdPublisher = publisherService.createPublisher(publisherDto);
            return ResponseEntity.ok(createdPublisher);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePublisher(@PathVariable String id, @Valid @RequestBody PublisherDto publisherDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponsePublisherDto updatedPublisher = publisherService.updatePublisher(id, publisherDto);
            return ResponseEntity.ok(updatedPublisher);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating publisher: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
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

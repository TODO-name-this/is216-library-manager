package com.todo.backend.controller;

import com.todo.backend.entity.Publisher;
import com.todo.backend.service.PublisherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Publisher> addPublisher(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody Publisher publisher) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Publisher savedPublisher = publisherService.createPublisher(publisher);
            return ResponseEntity.ok(savedPublisher);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{publisherId}")
    public ResponseEntity<Publisher> updatePublisher(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable String publisherId,
                                                     @RequestBody Publisher updatedPublisher) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Publisher updated = publisherService.updatePublisher(publisherId, updatedPublisher);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{publisherId}")
    public ResponseEntity<Void> deletePublisher(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable String publisherId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            publisherService.deletePublisher(publisherId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isAdmin(Jwt jwt) {
        String role = jwt.getClaimAsString("userType");
        return "admin".equalsIgnoreCase(role);
    }
}

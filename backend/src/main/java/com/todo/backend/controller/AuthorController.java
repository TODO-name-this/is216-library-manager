package com.todo.backend.controller;

import com.todo.backend.entity.Author;
import com.todo.backend.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Author> addAuthor(@AuthenticationPrincipal Jwt jwt,
                                            @RequestBody Author author) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Author saved = authorService.createAuthor(author);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{authorId}")
    public ResponseEntity<Author> updateAuthor(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable String authorId,
                                               @RequestBody Author updatedAuthor) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Author updated = authorService.updateAuthor(authorId, updatedAuthor);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{authorId}")
    public ResponseEntity<Void> deleteAuthor(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable String authorId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            authorService.deleteAuthor(authorId);
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

package com.todo.backend.controller;

import com.todo.backend.dto.author.AuthorDto;
import com.todo.backend.dto.author.ResponseAuthorDto;
import com.todo.backend.entity.Author;
import com.todo.backend.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllAuthors() {
        try {
            return ResponseEntity.ok(authorService.getAllAuthors());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching authors: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable String id) {
        try {
            ResponseAuthorDto author = authorService.getAuthor(id);
            return ResponseEntity.ok(author);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping
    public ResponseEntity<?> createAuthor(@Valid @RequestBody AuthorDto authorDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseAuthorDto createdAuthor = authorService.createAuthor(authorDto);
            return ResponseEntity.ok(createdAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable String id, @Valid @RequestBody AuthorDto authorDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseAuthorDto updatedAuthor = authorService.updateAuthor(id, authorDto);
            return ResponseEntity.ok(updatedAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating author: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable String id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.ok("Author deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting author: " + e.getMessage());
        }
    }
}

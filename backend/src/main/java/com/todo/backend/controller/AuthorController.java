package com.todo.backend.controller;

import com.todo.backend.entity.Author;
import com.todo.backend.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/author")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<?> createAuthor(@Valid @RequestBody Author author, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Author createdAuthor = authorService.createAuthor(author);
            return ResponseEntity.ok(createdAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating author: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable String id, @Valid @RequestBody Author author, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            Author updatedAuthor = authorService.updateAuthor(author);
            return ResponseEntity.ok(updatedAuthor);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating author: " + e.getMessage());
        }
    }

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

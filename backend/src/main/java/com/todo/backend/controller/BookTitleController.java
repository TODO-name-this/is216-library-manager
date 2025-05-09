package com.todo.backend.controller;

import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dto.BookTitleDto;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.service.BookTitleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// TODO: Need security

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/bookTitle")
public class BookTitleController {
    private final BookTitleService bookTitleService;

    public BookTitleController(BookTitleService bookTitleService) {
        this.bookTitleService = bookTitleService;
    }

    @PostMapping
    public ResponseEntity<?> createBookTitle(@Valid @RequestBody BookTitleDto bookTitleDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            BookTitle createdBookTitle = bookTitleService.createBookTitle(bookTitleDto);
            return ResponseEntity.ok(createdBookTitle);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating book title: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookTitle(@PathVariable String id, @Valid @RequestBody BookTitleDto bookTitleDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            BookTitle updatedBookTitle = bookTitleService.updateBookTitle(bookTitleDto);
            return ResponseEntity.ok(updatedBookTitle);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating book title: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookTitle(@PathVariable String id) {
        try {
            bookTitleService.deleteBookTitle(id);
            return ResponseEntity.ok("Book title deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting book title: " + e.getMessage());
        }
    }
}
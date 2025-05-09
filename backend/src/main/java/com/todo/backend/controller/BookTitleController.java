package com.todo.backend.controller;

import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.dto.booktitle.ResponseBookTitleDto;
import com.todo.backend.service.BookTitleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/bookTitle")
public class BookTitleController {
    private final BookTitleService bookTitleService;

    public BookTitleController(BookTitleService bookTitleService) {
        this.bookTitleService = bookTitleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookTitle(@PathVariable String id) {
        try {
            ResponseBookTitleDto bookTitle = bookTitleService.getBookTitle(id);
            return ResponseEntity.ok(bookTitle);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book title: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBookTitle(@Valid @RequestBody BookTitleDto bookTitleDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseBookTitleDto createdBookTitle = bookTitleService.createBookTitle(bookTitleDto);
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

            ResponseBookTitleDto updatedBookTitle = bookTitleService.updateBookTitle(id, bookTitleDto);
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
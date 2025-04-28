package com.todo.backend.controller;

import com.todo.backend.entity.BookCopy;
import com.todo.backend.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/bookCopy")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PostMapping
    public ResponseEntity<?> createBookCopy(@Valid @RequestBody BookCopy bookCopy, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            BookCopy createdBookCopy = bookCopyService.createBookCopy(bookCopy);
            return ResponseEntity.ok(createdBookCopy);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating book copy: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookCopy(@PathVariable String id, @Valid @RequestBody BookCopy bookCopy, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            BookCopy updatedBookCopy = bookCopyService.updateBookCopy(bookCopy);
            return ResponseEntity.ok(updatedBookCopy);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating book copy: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookCopy(@PathVariable String id) {
        try {
            bookCopyService.deleteBookCopy(id);
            return ResponseEntity.ok("Book copy deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting book copy: " + e.getMessage());
        }
    }
}

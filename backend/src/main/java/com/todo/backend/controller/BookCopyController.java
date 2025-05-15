package com.todo.backend.controller;

import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookCopy(@PathVariable String id) {
        try {
            BookCopy bookCopy = bookCopyService.getBookCopy(id);
            return ResponseEntity.ok(bookCopy);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book copy: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBookCopy(@Valid @RequestBody BookCopyDto bookCopyDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseBookCopyDto createdBookCopy = bookCopyService.createBookCopy(bookCopyDto);
            return ResponseEntity.ok(createdBookCopy);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating book copy: " + e.getMessage());
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

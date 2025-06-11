package com.todo.backend.controller;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.service.BookCopyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/bookCopy")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
public class BookCopyController {
    private final BookCopyService bookCopyService;
    private final BookCopyRepository bookCopyRepository;

    // Endpoint to get all book copies with due information
    // Endpoint: GET /api/bookCopy/all
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllBookCopiesWithDueInfo() {
        try {
            return ResponseEntity.ok(bookCopyRepository.findAllBookCopiesWithDueInfo());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book copies with due info: " + e.getMessage());
        }
    }
    // Endpoint to get overdue book copies
    // Endpoint: GET /api/bookCopy/overdue
    @GetMapping("/overdue")
    public ResponseEntity<?> getOverdueBookCopies() {
        try {
            return ResponseEntity.ok(bookCopyRepository.findOverdueBookCopies());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching overdue book copies: " + e.getMessage());
        }
    }
    // Endpoint to get a book copy by ID with overdue information
    // Endpoint: GET /api/bookCopy/overdue/{bookCopyId}
    @GetMapping("/overdue/{bookCopyId}")
    public ResponseEntity<?> getBookCopyWithDueInfo(@PathVariable String bookCopyId) {
        try {
            return ResponseEntity.ok(bookCopyRepository.findBookCopyWithDueInfo(bookCopyId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book copy with due info: " + e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllBookCopies() {
        try {
            return ResponseEntity.ok(bookCopyService.getAllBookCopies());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book copies: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookCopy(@PathVariable String id) {
        try {
            ResponseBookCopyDto bookCopy = bookCopyService.getBookCopyDto(id);
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

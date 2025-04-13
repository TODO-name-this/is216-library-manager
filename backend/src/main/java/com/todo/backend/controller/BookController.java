package com.todo.backend.controller;

import com.todo.backend.entity.Book;
import com.todo.backend.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Book> addBook(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody Book book) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Book savedBook = bookService.createBook(book);
            return ResponseEntity.ok(savedBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{bookId}")
    public ResponseEntity<Book> updateBook(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable String bookId,
                                           @RequestBody Book updatedBook) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Book updated = bookService.updateBook(bookId, updatedBook);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{bookId}")
    public ResponseEntity<Void> deleteBook(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable String bookId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            bookService.deleteBook(bookId);
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

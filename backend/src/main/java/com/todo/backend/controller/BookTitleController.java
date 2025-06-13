package com.todo.backend.controller;

import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.dto.booktitle.ResponseBookTitleDto;
import com.todo.backend.service.BookTitleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/bookTitle")
public class BookTitleController {
    private final BookTitleService bookTitleService;

    public BookTitleController(BookTitleService bookTitleService) {
        this.bookTitleService = bookTitleService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllBookTitles() {
        try {
            return ResponseEntity.ok(bookTitleService.getAllBookTitles());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book titles: " + e.getMessage());
        }
    }

    @GetMapping("/names")
    public ResponseEntity<?> getAllBookTitlesWithCategoryAndAuthorName() {
        try {
            return ResponseEntity.ok(bookTitleService.getAllBookTitlesWithCategoryAndAuthorName());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book titles with category and author names: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookTitle(@PathVariable String id) {
        try {
            // Get current user information if authenticated
            String currentUserId = null;
            boolean isUserRole = false;
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !(authentication.getPrincipal() instanceof String)) { // Not "anonymousUser"
                
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                currentUserId = userDetails.getUsername(); // This is the user ID
                
                // Check if user has USER role
                isUserRole = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> "USER".equals(auth.getAuthority()));
            }
            
            ResponseBookTitleDto bookTitle = bookTitleService.getBookTitleWithAvailability(id, currentUserId, isUserRole);
            return ResponseEntity.ok(bookTitle);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching book title: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
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
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookTitle(@PathVariable String id) {
        try {
            bookTitleService.deleteBookTitle(id);
            return ResponseEntity.ok("Book title deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting book title: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @PostMapping("/{id}/reconcile-inventory")
    public ResponseEntity<?> reconcileInventory(@PathVariable String id) {
        try {
            bookTitleService.reconcileInventory(id);
            return ResponseEntity.ok("Inventory reconciled successfully for book title: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error reconciling inventory: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/{id}/can-reserve")
    public ResponseEntity<?> canUserReserveBook(@PathVariable String id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            boolean canReserve = bookTitleService.canUserReserveBook(id, userId);
            return ResponseEntity.ok(Map.of("canReserve", canReserve));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking reservation availability: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/check-availability")
    public ResponseEntity<?> checkAvailability(@PathVariable String id) {
        try {
            ResponseBookTitleDto bookTitle = bookTitleService.getBookTitleWithAvailability(id, null, false);
            
            Map<String, Object> availabilityInfo = Map.of(
                "bookTitleId", id,
                "totalCopies", bookTitle.getTotalCopies(),
                "availableCopies", bookTitle.getAvailableCopies(),
                "pendingReservations", bookTitle.getOnlineReservations(),
                "maxOnlineReservations", bookTitle.getMaxOnlineReservations(),
                "canReserve", bookTitle.getAvailableCopies() > 0,
                "reservationStatus", bookTitle.getAvailableCopies() > 0 ? "AVAILABLE" : "FULL"
            );
            
            return ResponseEntity.ok(availabilityInfo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error checking availability: " + e.getMessage());
        }
    }
}
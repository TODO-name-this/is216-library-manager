package com.todo.backend.controller;

import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.dto.review.ReviewDto;
import com.todo.backend.dto.review.UpdateReviewDto;
import com.todo.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllReviews() {
        try {
            return ResponseEntity.ok(reviewService.getAllReviews());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching reviews: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReview(@PathVariable String id) {
        try {
            ResponseReviewDto review = reviewService.getReview(id);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching review: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto reviewDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseReviewDto createdReview = reviewService.createReview(userId, reviewDto);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating review: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id, @Valid @RequestBody UpdateReviewDto updateReviewDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseReviewDto updatedReview = reviewService.updateReview(id, userId, updateReviewDto);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating review: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            reviewService.deleteReview(id, userId);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting review: " + e.getMessage());
        }
    }
    
    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @GetMapping("/my-review/{bookTitleId}")
    public ResponseEntity<?> getMyReviewForBook(@PathVariable String bookTitleId, Authentication authentication) {
        try {
            String userId = authentication.getName();
            ResponseReviewDto review = reviewService.getUserReviewForBook(userId, bookTitleId);
            
            if (review == null) {
                return ResponseEntity.ok(null); // User has not reviewed this book
            }
            
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user review: " + e.getMessage());
        }
    }
}

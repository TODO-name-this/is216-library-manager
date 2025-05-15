package com.todo.backend.controller;

import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.dto.review.ReviewDto;
import com.todo.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
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

    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDto reviewDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseReviewDto createdReview = reviewService.createReview(reviewDto);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating review: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id, @Valid @RequestBody ReviewDto reviewDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseReviewDto updatedReview = reviewService.updateReview(id, reviewDto);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating review: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable String id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Review deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting review: " + e.getMessage());
        }
    }
}

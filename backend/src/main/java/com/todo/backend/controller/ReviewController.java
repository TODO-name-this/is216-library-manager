package com.todo.backend.controller;

import com.todo.backend.entity.Review;
import com.todo.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Review> addReview(@AuthenticationPrincipal Jwt jwt,
                                            @Valid @RequestBody Review review) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Review savedReview = reviewService.createReview(review);
            return ResponseEntity.ok(savedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{reviewId}")
    public ResponseEntity<Review> updateReview(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable String reviewId,
                                               @RequestBody Review updatedReview) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Review updated = reviewService.updateReview(reviewId, updatedReview);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal Jwt jwt,
                                             @PathVariable String reviewId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            reviewService.deleteReview(reviewId);
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

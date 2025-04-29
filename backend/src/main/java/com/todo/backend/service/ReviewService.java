package com.todo.backend.service;

import com.todo.backend.dao.ReviewRepository;
import com.todo.backend.entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(Review review) {
        if (reviewRepository.existsById(review.getId())) {
            throw new IllegalArgumentException("Review with ID already exists");
        }

        return reviewRepository.save(review);
    }

    public Review updateReview(Review review) {
        if (!reviewRepository.existsById(review.getId())) {
            throw new IllegalArgumentException("Review with ID does not exist");
        }

        return reviewRepository.save(review);
    }

    public void deleteReview(String id) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        reviewRepository.delete(existingReview);
    }
}

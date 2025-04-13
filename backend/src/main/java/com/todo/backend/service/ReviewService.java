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
            throw new IllegalArgumentException("Review already exists!");
        }
        return reviewRepository.save(review);
    }

    public Review updateReview(String id, Review updatedReview) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist: " + id));

        if (updatedReview.getTitle() != null)
            existing.setTitle(updatedReview.getTitle());

        if (updatedReview.getComment() != null)
            existing.setComment(updatedReview.getComment());

        if (updatedReview.getDate() != null)
            existing.setDate(updatedReview.getDate());

        if (updatedReview.getScore() != 0)
            existing.setScore(updatedReview.getScore());

        return reviewRepository.save(existing);
    }

    public void deleteReview(String id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist: " + id));
        reviewRepository.delete(existing);
    }
}

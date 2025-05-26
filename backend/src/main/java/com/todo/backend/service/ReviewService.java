package com.todo.backend.service;

import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReviewRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.dto.review.ReviewDto;
import com.todo.backend.entity.Review;
import com.todo.backend.mapper.ReviewMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookTitleRepository bookTitleRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository, BookTitleRepository bookTitleRepository, UserRepository userRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.bookTitleRepository = bookTitleRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    public ResponseReviewDto getReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        return reviewMapper.toResponseDto(review);
    }

    public ResponseReviewDto createReview(String userId, ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        review.setUserId(userId);

        validateReview(review);

        reviewRepository.save(review);

        return reviewMapper.toResponseDto(review);
    }

    public ResponseReviewDto updateReview(String id, String userId, ReviewDto reviewDto) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        if (!existingReview.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to update this review");
        }

        Review updatedReview = reviewMapper.toEntity(reviewDto);
        validateReview(updatedReview);

        reviewMapper.updateEntityFromDto(reviewDto, existingReview);

        reviewRepository.save(existingReview);

        return reviewMapper.toResponseDto(existingReview);
    }

    public void deleteReview(String id, String userId) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        if (!existingReview.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to delete this review");
        }

        reviewRepository.delete(existingReview);
    }

    private void validateReview(Review review) {
        if (!userRepository.existsById(review.getUserId())) {
            throw new IllegalArgumentException("User with this ID does not exist");
        }

        if (!bookTitleRepository.existsById(review.getBookTitleId())) {
            throw new IllegalArgumentException("Book title with this ID does not exist");
        }
    }
}

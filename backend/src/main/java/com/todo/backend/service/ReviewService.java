package com.todo.backend.service;

import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReviewRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.dto.review.ReviewDto;
import com.todo.backend.dto.review.UpdateReviewDto;
import com.todo.backend.entity.Review;
import com.todo.backend.mapper.ReviewMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ResponseReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(this::buildEnhancedResponseReviewDto)
                .toList();
    }

    public ResponseReviewDto getReview(String id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        return buildEnhancedResponseReviewDto(review);
    }
    
    public ResponseReviewDto getUserReviewForBook(String userId, String bookTitleId) {
        Review review = reviewRepository.findByUserIdAndBookTitleId(userId, bookTitleId);
        if (review == null) {
            return null; // User has not reviewed this book
        }
        return buildEnhancedResponseReviewDto(review);
    }

    public ResponseReviewDto createReview(String userId, ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        review.setUserId(userId);

        validateReview(review);
        
        // Check if user already has a review for this book
        if (reviewRepository.existsByUserIdAndBookTitleId(userId, reviewDto.getBookTitleId())) {
            throw new IllegalArgumentException("You have already reviewed this book. You can only leave one review per book.");
        }

        reviewRepository.save(review);

        return buildEnhancedResponseReviewDto(review);
    }

    public ResponseReviewDto updateReview(String id, String userId, UpdateReviewDto updateReviewDto) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

        if (!existingReview.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to update this review");
        }

        reviewMapper.updateEntityFromUpdateDto(updateReviewDto, existingReview);

        reviewRepository.save(existingReview);

        return buildEnhancedResponseReviewDto(existingReview);
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
    
    private ResponseReviewDto buildEnhancedResponseReviewDto(Review review) {
        ResponseReviewDto dto = reviewMapper.toResponseDto(review);
        
        // Add obfuscated user name (last word only)
        if (review.getUser() != null && review.getUser().getName() != null) {
            String fullName = review.getUser().getName();
            String[] nameParts = fullName.trim().split("\\s+");
            String obfuscatedName = nameParts[nameParts.length - 1]; // Get last word
            dto.setUserName(obfuscatedName);
        }
        
        return dto;
    }
}

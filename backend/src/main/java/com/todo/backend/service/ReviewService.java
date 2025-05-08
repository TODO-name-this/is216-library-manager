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

        return reviewMapper.toResponseReviewDto(review);
    }

    public ResponseReviewDto createReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);

        validateReview(review);

        reviewRepository.save(review);

        return reviewMapper.toResponseReviewDto(review);
    }

    public ResponseReviewDto updateReview(String id, ReviewDto reviewDto) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));


        Review updatedReview = reviewMapper.toEntity(reviewDto);
        validateReview(updatedReview);

        reviewMapper.updateEntityFromDto(reviewDto, existingReview);

        reviewRepository.save(existingReview);

        return reviewMapper.toResponseReviewDto(existingReview);
    }

    public void deleteReview(String id) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review with ID does not exist"));

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

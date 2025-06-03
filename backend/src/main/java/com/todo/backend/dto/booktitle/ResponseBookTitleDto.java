package com.todo.backend.dto.booktitle;

import com.todo.backend.dto.review.ResponseReviewDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResponseBookTitleDto {
    private String id;
    private String imageUrl;
    private String title;
    private String isbn;
    private boolean canBorrow;
    private int price;
    private LocalDate publishedDate;
    private String publisherId;    private List<String> authorIds;

    private List<String> categoryIds;
    
    private List<String> authorNames;

    private List<String> categoryNames;
    
    private List<ResponseReviewDto> reviews;
    
    // Availability information
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer onlineReservations;
    private Integer maxOnlineReservations;
    
    // User-specific information (only for authenticated users with USER role)
    private Integer userReservationsForThisBook;
    private Integer maxUserReservations;
}

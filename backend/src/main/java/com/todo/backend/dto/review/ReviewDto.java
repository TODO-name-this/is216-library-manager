package com.todo.backend.dto.review;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReviewDto {
    @NotNull(message = "Date of review is required")
    @PastOrPresent(message = "Date of review must be in the past or present")
    private LocalDate date;

    private String comment;

    @Min(value = 1, message = "Star must be at least 1")
    @Max(value = 5, message = "Star must be no more than 5")
    private int star;

    @NotBlank(message = "Book title ID is required")
    private String bookTitleId;
}

package com.todo.backend.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseReviewDto {
    private String id;
    private LocalDate date;
    private String comment;
    private int star;
    private String bookTitleId;
    private String userId;
}

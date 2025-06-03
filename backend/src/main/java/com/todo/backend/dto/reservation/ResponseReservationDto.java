package com.todo.backend.dto.reservation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResponseReservationDto {
    private String id;
    private LocalDate reservationDate;
    private LocalDate expirationDate;
    private int deposit;
    private String bookTitleId;
    private String bookCopyId;
    private String userId;
      // Enhanced book details
    private String bookTitle;
    private String bookImageUrl;
    private List<String> bookAuthors;
}

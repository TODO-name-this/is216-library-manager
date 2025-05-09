package com.todo.backend.dto.reservation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseReservationDto {
    private String id;
    private LocalDate reservationDate;
    private LocalDate expirationDate;
    private String status;
    private int deposit;
    private String bookTitleId;
    private String bookCopyId;
    private String userId;
}

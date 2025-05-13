package com.todo.backend.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservationDto {
    @NotNull(message = "Reservation date is required")
    @FutureOrPresent(message = "Reservation date must be today or in the future")
    private LocalDate reservationDate;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    @NotBlank(message = "Status is required")
    private String status;

    @Min(value = 0, message = "Deposit must be positive or zero")
    private int deposit;

    @NotBlank(message = "Book title ID is required")
    private String bookTitleId;

    @NotBlank(message = "Book copy ID is required")
    private String userId;
}

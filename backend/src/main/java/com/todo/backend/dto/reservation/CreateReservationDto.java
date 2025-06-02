package com.todo.backend.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateReservationDto {
    @NotBlank(message = "Book title ID is required")
    private String bookTitleId;
}

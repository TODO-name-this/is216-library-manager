package com.todo.backend.dto.reservation;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UpdateReservationDto {
    // Currently no fields that users can update
    // Status changes are handled by librarians/admins through separate endpoints
}

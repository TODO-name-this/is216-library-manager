package com.todo.backend.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartialUpdateReservationDto {
    @NotBlank(message = "Status is required")
    private String status;
}

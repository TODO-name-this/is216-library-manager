package com.todo.backend.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DamagedBookCopyDto {
    @NotBlank(message = "BookCopy ID cannot be null or empty")
    private String bookCopyId;

    @Min(value = 0, message = "Penalty fee must be at least 0")
    private int penaltyFee;
}

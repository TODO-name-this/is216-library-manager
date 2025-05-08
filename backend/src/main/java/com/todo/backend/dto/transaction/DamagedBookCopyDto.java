package com.todo.backend.dto.transaction;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DamagedBookCopyDto {
    @Valid
    @NotBlank(message = "BookCopy ID cannot be null or empty")
    private String bookCopyId;

    @Valid
    @NotNull(message = "Penalty fee cannot be null or empty")
    private int penaltyFee;
}

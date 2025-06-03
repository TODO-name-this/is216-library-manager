package com.todo.backend.dto.transactiondetail;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTransactionDetailDto {
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @Min(value = 0, message = "Penalty fee must be positive or zero")
    private int penaltyFee;

    private String description;
}

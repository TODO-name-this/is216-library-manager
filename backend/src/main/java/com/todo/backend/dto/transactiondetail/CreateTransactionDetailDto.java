package com.todo.backend.dto.transactiondetail;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateTransactionDetailDto {
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Book copy ID is required")
    private String bookCopyId;

    private LocalDate returnedDate;

    @Min(value = 0, message = "Penalty fee must be positive or zero")
    private int penaltyFee;
}

package com.todo.backend.dto.transaction;

import com.todo.backend.entity.BookCopyCondition;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReturnBookDto {
    @NotNull(message = "Return date is required")
    private LocalDate returnedDate;

    @NotNull(message = "Book condition is required")
    private BookCopyCondition bookCondition;

    private String description;

    @Min(value = 0, message = "Additional penalty fee must be positive or zero")
    private int additionalPenaltyFee;
}

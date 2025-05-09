package com.todo.backend.dto.transaction;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UpdateTransactionDto {
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotNull(message = "Missing returned book copies list")
    List<String> returnedBookCopyIds;

    @NotNull(message = "Missing damaged book copies list")
    List<DamagedBookCopyDto> damagedBookCopyIds;
}

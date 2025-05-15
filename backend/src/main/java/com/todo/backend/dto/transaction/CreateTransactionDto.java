package com.todo.backend.dto.transaction;

import com.todo.backend.entity.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateTransactionDto {
    @NotNull(message = "DueDate is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Missing bookCopy ids")
    private List<String> bookCopyIds;
}

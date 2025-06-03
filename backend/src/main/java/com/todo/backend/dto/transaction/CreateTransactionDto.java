package com.todo.backend.dto.transaction;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTransactionDto {
    @NotNull(message = "DueDate is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Book copy ID is required")
    private String bookCopyId;
}

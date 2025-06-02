package com.todo.backend.dto.bookcopy;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookCopyDto {
    @NotBlank(message = "Book copy ID is required")
    private String bookTitleId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private String condition; // NEW, GOOD, WORN, DAMAGED
}

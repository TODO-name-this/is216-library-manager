package com.todo.backend.dto.bookcopy;

import com.todo.backend.entity.BookCopyCondition;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateBookCopyDto {
    @NotBlank(message = "Book copy ID is required")
    private String bookTitleId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Nullable
    private BookCopyCondition condition;
}

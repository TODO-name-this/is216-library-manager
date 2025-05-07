package com.todo.backend.dto;

import com.todo.backend.entity.BookTitle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookTitleDto {
    @Valid
    @NotNull(message = "Book title cannot be null")
    private BookTitle bookTitle;

    private List<String> authorIds;
    private List<String> categoryIds;
}

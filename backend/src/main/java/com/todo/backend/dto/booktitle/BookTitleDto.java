package com.todo.backend.dto.booktitle;

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

    @Valid
    @NotNull(message = "Missing author ids")
    private List<String> authorIds;

    @Valid
    @NotNull(message = "Missing category ids")
    private List<String> categoryIds;
}

package com.todo.backend.dto;

import com.todo.backend.entity.BookTitle;
import lombok.Data;

import java.util.List;

@Data
public class BookTitleDto {
    private BookTitle bookTitle;
    private List<String> authorIds;
    private List<String> categoryIds;
}

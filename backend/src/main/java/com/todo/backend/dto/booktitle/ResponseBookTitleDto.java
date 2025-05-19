package com.todo.backend.dto.booktitle;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResponseBookTitleDto {
    private String id;
    private String imageUrl;
    private String title;
    private String isbn;
    private boolean canBorrow;
    private LocalDate publishedDate;
    private String publisherId;

    private List<String> authorIds;

    private List<String> categoryIds;
}

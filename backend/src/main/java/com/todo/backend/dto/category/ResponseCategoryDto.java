package com.todo.backend.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseCategoryDto {
    private String id;
    private String name;
    private String description;
}

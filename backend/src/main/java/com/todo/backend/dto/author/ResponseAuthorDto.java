package com.todo.backend.dto.author;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseAuthorDto {
    private String id;
    private String avatarUrl;
    private String name;
    private LocalDate birthday;
    private String biography;
}

package com.todo.backend.dto.author;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResponseAuthorDto {
    private String id;
    private String avatarUrl;
    private String name;
    private LocalDate birthday;
    private String biography;
    private List<String> bookIds;
    private List<String> bookNames;
    private List<String> bookUrls;
    private List<String> publisherIds;
    private List<String> publisherNames;
}

package com.todo.backend.dto.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@Builder
public class AuthorDto {
    @URL(message = "Invalid URL format")
    private String avatarUrl;

    @NotBlank(message = "Name is required")
    private String name;

    @Past(message = "Date of birth must be in the past")
    private LocalDate birthday;

    private String biography;
}

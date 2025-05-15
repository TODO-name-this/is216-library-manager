package com.todo.backend.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@Builder
public class UserDto {
    @NotBlank(message = "CCCD is required")
    private String cccd;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @URL(message = "Invalid URL format")
    private String avatarUrl;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @Min(value = 0, message = "Balance must be positive or zero")
    private int balance;
}
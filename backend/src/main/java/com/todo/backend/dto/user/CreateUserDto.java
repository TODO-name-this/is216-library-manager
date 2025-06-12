package com.todo.backend.dto.user;

import com.todo.backend.entity.identity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@Builder
public class CreateUserDto {
    @NotBlank(message = "CCCD is required")
    private String cccd;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @URL(message = "Invalid URL format")
    private String avatarUrl;

    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    @Min(value = 0, message = "Balance must be positive or zero")
    private int balance;
}
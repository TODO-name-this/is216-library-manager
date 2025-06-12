package com.todo.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Data
@Builder
public class SelfUpdateUserDto {
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @URL(message = "Invalid URL format")
    private String avatarUrl;

    private String name;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 digits")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;
}

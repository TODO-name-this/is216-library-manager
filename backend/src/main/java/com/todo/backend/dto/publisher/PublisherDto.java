package com.todo.backend.dto.publisher;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class PublisherDto {
    @URL(message = "Invalid URL format")
    private String logoUrl;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 digits")
    private String phone;
}

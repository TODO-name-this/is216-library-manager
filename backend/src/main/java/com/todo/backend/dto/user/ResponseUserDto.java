package com.todo.backend.dto.user;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseUserDto {
    private String id;
    private String cccd;
    private LocalDate dob;
    private String avatarUrl;
    private String name;
    private String email;
    private String role;
    private int balance;
}

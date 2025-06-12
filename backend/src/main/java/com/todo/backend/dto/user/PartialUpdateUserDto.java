package com.todo.backend.dto.user;

import com.todo.backend.entity.identity.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PartialUpdateUserDto {
    private String cccd;
    private LocalDate dob;
    private String avatarUrl;
    private String name;
    private String phone;
    private String email;
    private String oldPassword;
    private String newPassword;
    private UserRole role;
    private int balance;
}
package com.todo.backend.utils.auth;

import com.todo.backend.entity.identity.UserRole;

public record JwtUserInfo(String userId, UserRole userRole) {}

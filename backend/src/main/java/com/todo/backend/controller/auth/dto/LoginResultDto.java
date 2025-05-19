package com.todo.backend.controller.auth.dto;

public record LoginResultDto(
        String accessToken,
        String refreshToken
) {}

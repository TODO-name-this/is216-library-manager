package com.todo.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserActivityDto {
    private String role;
    private long count;
}

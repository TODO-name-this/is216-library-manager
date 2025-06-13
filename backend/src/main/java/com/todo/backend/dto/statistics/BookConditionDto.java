package com.todo.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookConditionDto {
    private String condition;
    private long count;
}

package com.todo.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PopularBookDto {
    private String title;
    private int borrowCount;
    private long revenue; // Total revenue from this book
}

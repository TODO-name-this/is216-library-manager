package com.todo.backend.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MonthlyStatDto {
    private String month;
    private int transactions;
    private long revenue;
}

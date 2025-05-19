package com.todo.backend.dto.transaction;

import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ResponseTransactionDto {
    private String id;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String userId;

    List<ResponseTransactionDetailDto> details;
}

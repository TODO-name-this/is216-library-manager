package com.todo.backend.dto.transactiondetail;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseTransactionDetailDto {
    private String transactionId;
    private String bookCopyId;
    private LocalDate returnedDate;
    private int penaltyFee;
}

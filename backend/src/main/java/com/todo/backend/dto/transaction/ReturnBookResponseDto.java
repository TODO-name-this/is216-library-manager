package com.todo.backend.dto.transaction;

import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnBookResponseDto {
    private ResponseTransactionDto transaction;
    private ResponseTransactionDetailDto transactionDetail;
    private int totalPenaltyFee;
    private int refundAmount;
    private String message;
}

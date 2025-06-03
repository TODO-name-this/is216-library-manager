package com.todo.backend.dto.transactiondetail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseTransactionDetailDto {
    private String transactionId;
    private int penaltyFee;
    private String description;
}

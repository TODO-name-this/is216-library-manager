package com.todo.backend.dto.transaction;

import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ResponseTransactionDto {
    private String id;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private String userId;
    private String bookCopyId;
    private LocalDate returnedDate;

    // Additional fields for enhanced response
    private String userName;
    private String bookTitle;

    private ResponseTransactionDetailDto transactionDetail;
}

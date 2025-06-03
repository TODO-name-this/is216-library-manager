package com.todo.backend.dto.bookcopy;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseBookCopyDto {
    private String id;
    private String bookTitleId;
    private String status; // AVAILABLE, BORROWED, MAINTENANCE, LOST, DAMAGED

    private String condition; // NEW, GOOD, WORN, DAMAGED

    // Book Title Information
    private String bookTitle;
    private String bookPhotoUrl;
    private Integer bookPrice;
    
    // User Information (borrower if BORROWED)
    private String borrowerCccd;
    private String borrowerName;
    private String borrowerId;
}

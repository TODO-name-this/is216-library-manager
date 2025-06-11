package com.todo.backend.dto.bookcopy;

import com.todo.backend.entity.BookCopyCondition;
import com.todo.backend.entity.BookCopyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseBookCopyDto {
    private String id;
    private String bookTitleId;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;
    @Enumerated(EnumType.STRING)
    private BookCopyCondition condition;

    // Book Title Information
    private String bookTitle;
    private String bookPhotoUrl;
    private Integer bookPrice;
    
    // User Information (borrower if BORROWED)
    private String borrowerCccd;
    private String borrowerName;
    private String borrowerId;
}

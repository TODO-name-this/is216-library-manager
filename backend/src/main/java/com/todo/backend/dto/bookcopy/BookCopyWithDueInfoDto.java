package com.todo.backend.dto.bookcopy;

import com.todo.backend.entity.BookCopyCondition;
import com.todo.backend.entity.BookCopyStatus;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyWithDueInfoDto {
    private String bookCopyId;
    @Enumerated(value = EnumType.STRING)
    private BookCopyStatus status;
    @Enumerated(value = EnumType.STRING)
    private BookCopyCondition condition;
    private String bookTitle;
    private String bookTitleId;
    private Integer bookPrice;
    private LocalDate dueDate;
    private LocalDate borrowDate;
    private String borrowerId;
    private String borrowerName;
    private String borrowerCccd;
    private Boolean isOverdue;
}
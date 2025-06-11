package com.todo.backend.dto.bookcopy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyWithDueInfoDto {
    private String bookCopyId;
    private String status;
    private String condition;
    private String bookTitle;
    private String bookTitleId;
    private LocalDate dueDate;
    private LocalDate borrowDate;
    private String borrowerId;
    private String borrowerName;
    private Boolean isOverdue;
}
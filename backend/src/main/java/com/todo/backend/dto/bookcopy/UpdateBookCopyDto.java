package com.todo.backend.dto.bookcopy;

import com.todo.backend.entity.BookCopyCondition;
import com.todo.backend.entity.BookCopyStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookCopyDto {
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;
    
    @NotNull(message = "Condition is required")
    @Enumerated(EnumType.STRING)
    private BookCopyCondition condition;
}

package com.todo.backend.dto;

import com.todo.backend.entity.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TransactionUpdateDto {
    @Valid
    @NotNull(message = "Transaction cannot be null")
    private Transaction transaction;

    @Valid
    @NotNull(message = "Missing returned book copies list")
    List<String> returnedBookCopies;

    @Valid
    @NotNull(message = "Missing damaged book copies list")
    List<DamagedBookCopyDto> damagedBookCopies;

    @Valid
    @NotNull(message = "Missing removed book copies list")
    List<String> removedBookCopies;
}

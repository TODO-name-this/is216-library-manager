package com.todo.backend.dto;

import com.todo.backend.entity.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TransactionCreateDto {
    @Valid
    @NotNull(message = "Transaction cannot be null")
    private Transaction transaction;

    @Valid
    @NotNull(message = "Missing bookCopy ids")
    private List<String> bookCopyIds;
}

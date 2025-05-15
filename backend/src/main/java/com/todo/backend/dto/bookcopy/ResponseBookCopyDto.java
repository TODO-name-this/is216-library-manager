package com.todo.backend.dto.bookcopy;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResponseBookCopyDto {
    List<String> bookCopyIds;
}

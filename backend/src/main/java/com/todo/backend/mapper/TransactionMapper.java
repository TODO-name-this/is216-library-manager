package com.todo.backend.mapper;

import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(CreateTransactionDto createTransactionDto);
    CreateTransactionDto toCreateDto(Transaction transaction);

    Transaction toEntity(UpdateTransactionDto updateTransactionDto);
    UpdateTransactionDto toUpdateDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateTransactionDto updateTransactionDto, @MappingTarget Transaction transaction);

    ResponseTransactionDto toResponseDto(Transaction transaction);
    List<ResponseTransactionDto> toResponseDtoList(List<Transaction> transactions);
}

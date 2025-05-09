package com.todo.backend.mapper;

import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.TransactionDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionDetailMapper {
    TransactionDetail toEntity(ResponseTransactionDetailDto responseTransactionDto);
    ResponseTransactionDetailDto toResponseDto(TransactionDetail transactionDetail);
}

package com.todo.backend.mapper;

import com.todo.backend.dto.transactiondetail.CreateTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.UpdateTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.TransactionDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionDetailMapper {
    TransactionDetail toEntity(CreateTransactionDetailDto createTransactionDetailDto);
    CreateTransactionDetailDto toCreateDto(TransactionDetail transactionDetail);

    TransactionDetail toEntity(UpdateTransactionDetailDto updateTransactionDetailDto);
    UpdateTransactionDetailDto toUpdateDto(TransactionDetail transactionDetail);

    void updateEntityFromDto(UpdateTransactionDetailDto updateTransactionDetailDto, @MappingTarget TransactionDetail transactionDetail);

    ResponseTransactionDetailDto toResponseDto(TransactionDetail transactionDetail);
    List<ResponseTransactionDetailDto> toResponseDtoList(List<TransactionDetail> transactionDetails);
}

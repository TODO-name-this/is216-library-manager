package com.todo.backend.service;

import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dto.transactiondetail.CreateTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.dto.transactiondetail.UpdateTransactionDetailDto;
import com.todo.backend.entity.TransactionDetail;
import com.todo.backend.entity.compositekey.TransactionDetailPrimaryKey;
import com.todo.backend.mapper.TransactionDetailMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TransactionDetailService {
    private final TransactionDetailRepository transactionDetailRepository;
    private final TransactionDetailMapper transactionDetailMapper;

    public TransactionDetailService(TransactionDetailRepository transactionDetailRepository, TransactionDetailMapper transactionDetailMapper) {
        this.transactionDetailRepository = transactionDetailRepository;
        this.transactionDetailMapper = transactionDetailMapper;
    }

    public List<ResponseTransactionDetailDto> getAllTransactionDetails() {
        List<TransactionDetail> transactionDetails = transactionDetailRepository.findAll();
        return transactionDetailMapper.toResponseDtoList(transactionDetails);
    }

    public ResponseTransactionDetailDto getTransactionDetail(String transactionId, String bookCopyId) {
        TransactionDetail transactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(transactionId, bookCopyId);
        if (transactionDetail == null) {
            throw new RuntimeException("Transaction detail not found");
        }
        return transactionDetailMapper.toResponseDto(transactionDetail);
    }

    public ResponseTransactionDetailDto createTransactionDetail(CreateTransactionDetailDto createTransactionDetailDto) {
        TransactionDetail transactionDetail = transactionDetailMapper.toEntity(createTransactionDetailDto);
        TransactionDetail savedTransactionDetail = transactionDetailRepository.save(transactionDetail);
        return transactionDetailMapper.toResponseDto(savedTransactionDetail);
    }

    public ResponseTransactionDetailDto updateTransactionDetail(String transactionId, String bookCopyId, UpdateTransactionDetailDto updateTransactionDetailDto) {
        TransactionDetail existingTransactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(transactionId, bookCopyId);
        if (existingTransactionDetail == null) {
            throw new RuntimeException("Transaction detail not found");
        }

        transactionDetailMapper.updateEntityFromDto(updateTransactionDetailDto, existingTransactionDetail);
        TransactionDetail updatedTransactionDetail = transactionDetailRepository.save(existingTransactionDetail);
        return transactionDetailMapper.toResponseDto(updatedTransactionDetail);
    }

    public void deleteTransactionDetail(String transactionId, String bookCopyId) {
        TransactionDetail existingTransactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(transactionId, bookCopyId);
        if (existingTransactionDetail == null) {
            throw new RuntimeException("Transaction detail not found");
        }

        transactionDetailRepository.delete(existingTransactionDetail);
    }
}

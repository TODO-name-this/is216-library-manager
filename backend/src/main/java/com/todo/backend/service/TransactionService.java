package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dto.transaction.DamagedBookCopyDto;
import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.TransactionDetail;
import com.todo.backend.mapper.TransactionDetailMapper;
import com.todo.backend.mapper.TransactionMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final BookCopyRepository bookCopyRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionDetailMapper transactionDetailMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionDetailRepository transactionDetailRepository, BookCopyRepository bookCopyRepository, TransactionMapper transactionMapper, TransactionDetailMapper transactionDetailMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.transactionMapper = transactionMapper;
        this.transactionDetailMapper = transactionDetailMapper;
    }

    public ResponseTransactionDto getTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        List<ResponseTransactionDetailDto> details = transaction.getTransactionDetails()
                .stream()
                .map(transactionDetailMapper::toResponseDto)
                .toList();

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
        responseTransactionDto.setDetails(details);

        return responseTransactionDto;
    }

    public ResponseTransactionDto createTransaction(CreateTransactionDto createTransactionDto) {
        LocalDate today = LocalDate.now();

        Transaction transaction = transactionMapper.toEntity(createTransactionDto);
        transaction.setBorrowDate(today);

        List<String> bookCopyIds = createTransactionDto.getBookCopyIds();
        List<TransactionDetail> unreturnedDetails = transactionDetailRepository.findByUserIdAndNotReturned(transaction.getUserId());
        List<String> unreturnedBookCopyIds = unreturnedDetails.stream()
                .map(TransactionDetail::getBookCopyId)
                .toList();

        if (bookCopyIds.size() != bookCopyIds.stream().distinct().count()) {
            throw new RuntimeException("Duplicate book copies found in the request");
        }

        if (bookCopyIds.isEmpty()) {
            throw new RuntimeException("Transaction must have at least one bookCopy");
        }

        if (bookCopyIds.size() > 5 || unreturnedDetails.size() + bookCopyIds.size() > 5) {
            throw new RuntimeException("User can only borrow 5 books at a time");
        }

        // Check if all books are available, only one book copy per book title is allowed,
        // and book title can be borrowed
        List<String> allBookTitles = new ArrayList<>(unreturnedBookCopyIds);

        for (String bookCopyId : bookCopyIds) {
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));

            if (bookCopy == null || !bookCopy.getStatus().equals("AVAILABLE")) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not available");
            }

            if (allBookTitles.contains(bookCopy.getBookTitleId())) {
                throw new RuntimeException("Only one book copy per book title is allowed");
            }

            allBookTitles.add(bookCopy.getBookTitleId());

            if (!bookCopy.getBookTitle().isCanBorrow()) {
                throw new RuntimeException("This book title cannot be borrowed");
            }

            bookCopy.setStatus("BORROWED");
        }

        transactionRepository.saveAndFlush(transaction);

        // Create TransactionDetail
        List<TransactionDetail> details = new ArrayList<>();
        for (String bookCopyId : bookCopyIds) {
            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setTransactionId(transaction.getId());
            transactionDetail.setBookCopyId(bookCopyId);
            transactionDetail.setReturnedDate(null);
            transactionDetail.setPenaltyFee(0);

            details.add(transactionDetail);
        }

        transaction.setTransactionDetails(details);
        transactionRepository.save(transaction);

        List<ResponseTransactionDetailDto> responseDetails = details.stream()
                .map(transactionDetailMapper::toResponseDto)
                .toList();

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
        responseTransactionDto.setDetails(responseDetails);

        return responseTransactionDto;
    }

    public ResponseTransactionDto updateTransaction(String id, UpdateTransactionDto updateTransactionDto) {
        LocalDate today = LocalDate.now();

        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        // Update transaction (due date)
        transactionMapper.updateEntityFromDto(updateTransactionDto, existingTransaction);

        List<String> returnedBookCopyIds = updateTransactionDto.getReturnedBookCopyIds();
        List<DamagedBookCopyDto> damagedBookCopyIds = updateTransactionDto.getDamagedBookCopyIds();
        List<String> borrowedBookCopyIds = new ArrayList<>(transactionDetailRepository.findByTransactionId(existingTransaction.getId())
                .stream()
                .map(TransactionDetail::getBookCopyId)
                .toList());

        // Check if any duplicate book copies
        List<String> allBookCopyIds = new ArrayList<>();
        allBookCopyIds.addAll(returnedBookCopyIds);
        allBookCopyIds.addAll(damagedBookCopyIds.stream().map(DamagedBookCopyDto::getBookCopyId).toList());

        if (allBookCopyIds.size() != allBookCopyIds.stream().distinct().count()) {
            throw new RuntimeException("Duplicate book copies found in the update request");
        }

        List<TransactionDetail> details = new ArrayList<>();

        // Update returned book copies
        for (String bookCopyId : returnedBookCopyIds) {
            if (!borrowedBookCopyIds.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            borrowedBookCopyIds.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("AVAILABLE");

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setTransactionId(existingTransaction.getId());
            transactionDetail.setBookCopyId(bookCopyId);
            transactionDetail.setReturnedDate(today);
            transactionDetail.setPenaltyFee(0);
            details.add(transactionDetail);
        }

        // Update damaged book copies
        for (DamagedBookCopyDto damagedBookCopy : damagedBookCopyIds) {
            String bookCopyId = damagedBookCopy.getBookCopyId();
            if (!borrowedBookCopyIds.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            borrowedBookCopyIds.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("DAMAGED");

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setTransactionId(existingTransaction.getId());
            transactionDetail.setBookCopyId(bookCopyId);
            transactionDetail.setReturnedDate(today);
            transactionDetail.setPenaltyFee(damagedBookCopy.getPenaltyFee());
            details.add(transactionDetail);
        }

        // Keep remaining transaction details
        for (String bookCopyId : borrowedBookCopyIds) {
            TransactionDetail transactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(existingTransaction.getId(), bookCopyId);
            if (transactionDetail != null) {
                details.add(transactionDetail);
            }
        }

        // Update transaction details
        existingTransaction.getTransactionDetails().clear();
        existingTransaction.getTransactionDetails().addAll(details);
        transactionRepository.save(existingTransaction);

        List<ResponseTransactionDetailDto> responseDetails = details.stream()
                .map(transactionDetailMapper::toResponseDto)
                .toList();

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(existingTransaction);
        responseTransactionDto.setDetails(responseDetails);

        return responseTransactionDto;
    }

    public void deleteTransaction(String id) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (existingTransaction.getTransactionDetails().stream().anyMatch(detail -> detail.getReturnedDate() == null)) {
            throw new RuntimeException("Cannot delete transaction. Not all books have been returned.");
        }

        transactionRepository.delete(existingTransaction);
    }
}

package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.transaction.DamagedBookCopyDto;
import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.TransactionDetail;
import com.todo.backend.entity.User;
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
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionDetailMapper transactionDetailMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionDetailRepository transactionDetailRepository, BookCopyRepository bookCopyRepository, UserRepository userRepository, TransactionMapper transactionMapper, TransactionDetailMapper transactionDetailMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
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

    public List<ResponseTransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream().map(transaction -> {
            List<ResponseTransactionDetailDto> details = transaction.getTransactionDetails()
                    .stream()
                    .map(transactionDetailMapper::toResponseDto)
                    .toList();

            ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
            responseTransactionDto.setDetails(details);
            return responseTransactionDto;
        }).toList();
    }

    public List<ResponseTransactionDto> getTransactionsByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream().map(transaction -> {
            List<ResponseTransactionDetailDto> details = transaction.getTransactionDetails()
                    .stream()
                    .map(transactionDetailMapper::toResponseDto)
                    .toList();

            ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
            responseTransactionDto.setDetails(details);
            return responseTransactionDto;
        }).toList();
    }

    public ResponseTransactionDto createTransaction(CreateTransactionDto createTransactionDto) {
        LocalDate today = LocalDate.now();

        Transaction transaction = transactionMapper.toEntity(createTransactionDto);
        transaction.setBorrowDate(today);

        List<String> bookCopyIds = createTransactionDto.getBookCopyIds();
        List<TransactionDetail> unreturnedDetails = transactionDetailRepository.findByUserIdAndNotReturned(transaction.getUserId());
        List<String> unreturnedBookTitleIds = unreturnedDetails.stream()
                .map(detail -> bookCopyRepository.findById(detail.getBookCopyId())
                        .orElseThrow(() -> new RuntimeException("BookCopy with ID " + detail.getBookCopyId() + " not found"))
                        .getBookTitleId())
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

        // Validate user balance before allowing transaction
        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + transaction.getUserId() + " not found"));
        
        int totalPrice = 0;
        for (String bookCopyId : bookCopyIds) {
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            totalPrice += bookCopy.getBookTitle().getPrice();
        }
        
        if (user.getBalance() < totalPrice) {
            throw new RuntimeException(String.format("Insufficient balance. Required: %,d VND, Available: %,d VND", 
                    totalPrice, user.getBalance()));
        }

        // Check if all books are available, only one book copy per book title is allowed,
        // and book title can be borrowed
        List<String> allBookTitles = new ArrayList<>(unreturnedBookTitleIds);

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

        // Deduct the total price from user's balance
        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);

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
        
        // Get user for balance updates
        User user = userRepository.findById(existingTransaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + existingTransaction.getUserId() + " not found"));
        
        int refundAmount = 0;

        // Update returned book copies
        for (String bookCopyId : returnedBookCopyIds) {
            if (!borrowedBookCopyIds.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            borrowedBookCopyIds.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("AVAILABLE");
            
            // Add to refund amount for properly returned books
            refundAmount += bookCopy.getBookTitle().getPrice();

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
          // Refund user balance for properly returned books
        if (refundAmount > 0) {
            user.setBalance(user.getBalance() + refundAmount);
            userRepository.save(user);
        }

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

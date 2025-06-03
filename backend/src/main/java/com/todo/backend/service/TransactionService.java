package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.Reservation;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.User;
import com.todo.backend.mapper.TransactionDetailMapper;
import com.todo.backend.mapper.TransactionMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionDetailMapper transactionDetailMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionDetailRepository transactionDetailRepository, BookCopyRepository bookCopyRepository, UserRepository userRepository, ReservationRepository reservationRepository, TransactionMapper transactionMapper, TransactionDetailMapper transactionDetailMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.transactionMapper = transactionMapper;
        this.transactionDetailMapper = transactionDetailMapper;
    }

    public ResponseTransactionDto getTransaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
        
        // Include transaction detail if it exists
        if (transaction.getTransactionDetail() != null) {
            ResponseTransactionDetailDto detail = transactionDetailMapper.toResponseDto(transaction.getTransactionDetail());
            responseTransactionDto.setTransactionDetail(detail);
        }

        return responseTransactionDto;
    }

    public List<ResponseTransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream().map(transaction -> {
            ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
            
            // Include transaction detail if it exists
            if (transaction.getTransactionDetail() != null) {
                ResponseTransactionDetailDto detail = transactionDetailMapper.toResponseDto(transaction.getTransactionDetail());
                responseTransactionDto.setTransactionDetail(detail);
            }

            return responseTransactionDto;
        }).toList();
    }

    public List<ResponseTransactionDto> getTransactionsByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream().map(transaction -> {
            ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(transaction);
            
            // Include transaction detail if it exists
            if (transaction.getTransactionDetail() != null) {
                ResponseTransactionDetailDto detail = transactionDetailMapper.toResponseDto(transaction.getTransactionDetail());
                responseTransactionDto.setTransactionDetail(detail);
            }

            return responseTransactionDto;
        }).toList();
    }

    public ResponseTransactionDto createTransaction(CreateTransactionDto createTransactionDto) {
        LocalDate today = LocalDate.now();

        Transaction transaction = transactionMapper.toEntity(createTransactionDto);
        transaction.setBorrowDate(today);

        String bookCopyId = createTransactionDto.getBookCopyId();
        
        // Check if user has too many unreturned books (limit 5)
        List<Transaction> unreturnedTransactions = transactionRepository.findByUserIdAndReturnedDateIsNull(transaction.getUserId());
        if (unreturnedTransactions.size() >= 5) {
            throw new RuntimeException("User can only borrow 5 books at a time");
        }

        // Validate user balance before allowing transaction
        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + transaction.getUserId() + " not found"));
        
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));

        if (!bookCopy.getStatus().equals("AVAILABLE")) {
            throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not available");
        }

        if (!bookCopy.getBookTitle().isCanBorrow()) {
            throw new RuntimeException("This book title cannot be borrowed");
        }

        int totalPrice = bookCopy.getBookTitle().getPrice();
        if (user.getBalance() < totalPrice) {
            throw new RuntimeException(String.format("Insufficient balance. Required: %,d VND, Available: %,d VND", 
                    totalPrice, user.getBalance()));
        }

        // Check if user already has this book title borrowed
        List<String> unreturnedBookTitleIds = unreturnedTransactions.stream()
                .map(t -> {
                    BookCopy bc = bookCopyRepository.findById(t.getBookCopyId())
                            .orElseThrow(() -> new RuntimeException("BookCopy with ID " + t.getBookCopyId() + " not found"));
                    return bc.getBookTitleId();
                })
                .toList();

        if (unreturnedBookTitleIds.contains(bookCopy.getBookTitleId())) {
            throw new RuntimeException("Only one book copy per book title is allowed");
        }

        // Update book copy status and save transaction
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Deduct the price from user's balance
        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);

        return transactionMapper.toResponseDto(savedTransaction);
    }

    public ResponseTransactionDto updateTransaction(String id, UpdateTransactionDto updateTransactionDto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        // Update due date and returned date
        transactionMapper.updateEntityFromDto(updateTransactionDto, existingTransaction);

        // If returned date is set, update book copy status and refund user
        if (updateTransactionDto.getReturnedDate() != null && existingTransaction.getReturnedDate() == null) {
            BookCopy bookCopy = bookCopyRepository.findById(existingTransaction.getBookCopyId())
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + existingTransaction.getBookCopyId() + " not found"));

            bookCopy.setStatus("AVAILABLE");
            bookCopyRepository.save(bookCopy);

            // Refund user balance
            User user = userRepository.findById(existingTransaction.getUserId())
                    .orElseThrow(() -> new RuntimeException("User with ID " + existingTransaction.getUserId() + " not found"));
            
            int refundAmount = bookCopy.getBookTitle().getPrice();
            user.setBalance(user.getBalance() + refundAmount);
            userRepository.save(user);
        }

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        
        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(updatedTransaction);
        
        // Include transaction detail if it exists
        if (updatedTransaction.getTransactionDetail() != null) {
            ResponseTransactionDetailDto detail = transactionDetailMapper.toResponseDto(updatedTransaction.getTransactionDetail());
            responseTransactionDto.setTransactionDetail(detail);
        }

        return responseTransactionDto;
    }

    public void deleteTransaction(String id) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (existingTransaction.getReturnedDate() == null) {
            throw new RuntimeException("Cannot delete transaction. Book has not been returned.");
        }

        transactionRepository.delete(existingTransaction);
    }

    public ResponseTransactionDto createTransactionFromReservation(String reservationId, String bookCopyId) {
        // Find and validate reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

        // Get user from reservation
        User user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + reservation.getUserId()));

        // Get the librarian-selected book copy
        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("Book copy not found: " + bookCopyId));

        // Validate the selected book copy matches the reserved book title
        if (!bookCopy.getBookTitleId().equals(reservation.getBookTitleId())) {
            throw new RuntimeException("Selected book copy does not match the reserved book title");
        }

        // Validate book copy is available for borrowing
        if (!"AVAILABLE".equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book copy is not available for borrowing. Current status: " + bookCopy.getStatus());
        }

        // Check user transaction limits
        List<Transaction> unreturnedTransactions = transactionRepository.findByUserIdAndReturnedDateIsNull(user.getId());
        if (unreturnedTransactions.size() >= 5) {
            throw new RuntimeException("User can only borrow 5 books at a time");
        }

        // Check if user already has this book title borrowed
        List<String> unreturnedBookTitleIds = unreturnedTransactions.stream()
                .map(t -> {
                    BookCopy bc = bookCopyRepository.findById(t.getBookCopyId())
                            .orElseThrow(() -> new RuntimeException("BookCopy with ID " + t.getBookCopyId() + " not found"));
                    return bc.getBookTitleId();
                })
                .toList();

        if (unreturnedBookTitleIds.contains(bookCopy.getBookTitleId())) {
            throw new RuntimeException("User already has a copy of this book title borrowed");
        }

        // Validate book title can be borrowed
        if (!bookCopy.getBookTitle().isCanBorrow()) {
            throw new RuntimeException("This book title cannot be borrowed");
        }

        // Check user balance (total price minus deposit already paid)
        int totalPrice = bookCopy.getBookTitle().getPrice();
        int remainingAmount = totalPrice - reservation.getDeposit();
        
        if (user.getBalance() < remainingAmount) {
            throw new RuntimeException(String.format(
                "Insufficient balance. Required: %,d VND (Total: %,d VND - Deposit: %,d VND), Available: %,d VND", 
                remainingAmount, totalPrice, reservation.getDeposit(), user.getBalance()));
        }

        // Create the transaction
        LocalDate today = LocalDate.now();
        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setBookCopyId(bookCopy.getId());
        transaction.setBorrowDate(today);
        transaction.setDueDate(today.plusWeeks(2)); // Default 2 weeks loan period

        // Update book copy status
        bookCopy.setStatus("BORROWED");
        bookCopyRepository.save(bookCopy);

        // Deduct remaining amount from user balance (deposit was already deducted during reservation)
        user.setBalance(user.getBalance() - remainingAmount);
        userRepository.save(user);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Delete the reservation (since it's now fulfilled)
        reservationRepository.delete(reservation);

        return transactionMapper.toResponseDto(savedTransaction);
    }
}

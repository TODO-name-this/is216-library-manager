package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.transaction.CreateTransactionDto;
import com.todo.backend.dto.transaction.ResponseTransactionDto;
import com.todo.backend.dto.transaction.UpdateTransactionDto;
import com.todo.backend.dto.transaction.ReturnBookDto;
import com.todo.backend.dto.transaction.ReturnBookResponseDto;
import com.todo.backend.dto.transactiondetail.ResponseTransactionDetailDto;
import com.todo.backend.entity.*;
import com.todo.backend.mapper.TransactionDetailMapper;
import com.todo.backend.mapper.TransactionMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

        // Enhance with user name and book title
        enhanceTransactionDto(responseTransactionDto, transaction);

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

            // Enhance with user name and book title
            enhanceTransactionDto(responseTransactionDto, transaction);

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

            // Enhance with user name and book title
            enhanceTransactionDto(responseTransactionDto, transaction);

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

        // Get unreturned transactions for duplicate book title check
        List<Transaction> unreturnedTransactions = transactionRepository.findByUserIdAndReturnedDateIsNull(transaction.getUserId());

        // Validate user balance before allowing transaction
        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + transaction.getUserId() + " not found"));

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));

        if (!bookCopy.getStatus().equals(BookCopyStatus.AVAILABLE)) {
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
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Deduct the price from user's balance
        user.setBalance(user.getBalance() - totalPrice);
        userRepository.save(user);

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(savedTransaction);

        // Enhance with user name and book title
        enhanceTransactionDto(responseTransactionDto, savedTransaction);

        return responseTransactionDto;
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

            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
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

        // Enhance with user name and book title
        enhanceTransactionDto(responseTransactionDto, updatedTransaction);

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
        if (!BookCopyStatus.AVAILABLE.equals(bookCopy.getStatus())) {
            throw new RuntimeException("Book copy is not available for borrowing. Current status: " + bookCopy.getStatus());
        }

        // Get unreturned transactions for duplicate book title check
        List<Transaction> unreturnedTransactions = transactionRepository.findByUserIdAndReturnedDateIsNull(user.getId());

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
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        // Deduct remaining amount from user balance (deposit was already deducted during reservation)
        user.setBalance(user.getBalance() - remainingAmount);
        userRepository.save(user);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Delete the reservation (since it's now fulfilled)
        reservationRepository.delete(reservation);

        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(savedTransaction);

        // Enhance with username and book title
        enhanceTransactionDto(responseTransactionDto, savedTransaction);

        return responseTransactionDto;
    }

    /**
     * Integrated book return with penalty calculation
     * @param isLost If true, treats the book as lost (penalty = bookPrice + late fees)
     */
    public ReturnBookResponseDto returnBook(String transactionId, ReturnBookDto returnBookDto, boolean isLost) {
        // Get the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        if (transaction.getReturnedDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }

        // Get related entities
        BookCopy bookCopy = bookCopyRepository.findById(transaction.getBookCopyId())
                .orElseThrow(() -> new RuntimeException("BookCopy with ID " + transaction.getBookCopyId() + " not found"));

        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + transaction.getUserId() + " not found"));

        // Calculate penalties with automatic late fee calculation
        int bookPrice = bookCopy.getBookTitle().getPrice();
        int automaticLateFee;
        int additionalPenalty = returnBookDto.getAdditionalPenaltyFee(); // damage fees, etc.
          if (isLost) {
            // Lost book: book replacement cost + capped late fees
            automaticLateFee = calculateLateFee(transaction, returnBookDto.getReturnedDate(), bookPrice); // Still capped
            additionalPenalty += bookPrice; // Add book replacement cost to additional penalty
        } else {
            // Normal return: late fee capped at book price
            automaticLateFee = calculateLateFee(transaction, returnBookDto.getReturnedDate(), bookPrice);
        }

        int totalPenaltyFee = automaticLateFee + additionalPenalty;
          // Note: Late fee is always capped at book price for both normal and lost returns
        // For lost books, user pays capped late fee + book replacement cost + any damage fees

        // Update transaction
        transaction.setReturnedDate(returnBookDto.getReturnedDate());
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Update book copy status and condition
        updateBookCopyForReturn(bookCopy, returnBookDto.getBookCondition(), isLost);

        // Create transaction detail if there are penalties or description
        TransactionDetail transactionDetail = null;
        if (totalPenaltyFee > 0 || returnBookDto.getDescription() != null) {
            String description = buildPenaltyDescription(automaticLateFee, additionalPenalty, returnBookDto.getDescription(), isLost, bookPrice);
            transactionDetail = createTransactionDetail(transactionId, totalPenaltyFee, description);
        }

        // Update user balance: user gets refund of (book price - penalty) or pays extra if penalty > book price
        int refundAmount = 0;
        int extraCharge = 0;
        
        if (totalPenaltyFee <= bookPrice) {
            // Penalty is covered by deposit, give partial refund
            refundAmount = bookPrice - totalPenaltyFee;
            user.setBalance(user.getBalance() + refundAmount);
        } else {
            // Penalty exceeds deposit, charge extra from user's balance
            extraCharge = totalPenaltyFee - bookPrice;
            user.setBalance(user.getBalance() - extraCharge);
        }
        userRepository.save(user);

        // Build response
        ResponseTransactionDto responseTransactionDto = transactionMapper.toResponseDto(updatedTransaction);
        enhanceTransactionDto(responseTransactionDto, updatedTransaction);

        ResponseTransactionDetailDto responseTransactionDetailDto = null;
        if (transactionDetail != null) {
            responseTransactionDetailDto = transactionDetailMapper.toResponseDto(transactionDetail);
            responseTransactionDto.setTransactionDetail(responseTransactionDetailDto);
        }

        String message = buildReturnMessage(automaticLateFee, additionalPenalty, refundAmount, bookPrice);

        return ReturnBookResponseDto.builder()
                .transaction(responseTransactionDto)
                .transactionDetail(responseTransactionDetailDto)
                .totalPenaltyFee(totalPenaltyFee)
                .refundAmount(refundAmount)
                .message(message)
                .build();
    }

    private void updateBookCopyForReturn(BookCopy bookCopy, BookCopyCondition newCondition, boolean isLost) {
        if (isLost) {
            // Lost book: mark as lost regardless of condition
            bookCopy.setStatus(BookCopyStatus.LOST);
            bookCopy.setCondition(BookCopyCondition.DAMAGED); // Assume lost books are damaged
        } else {
            bookCopy.setCondition(newCondition);

            // Set status based on condition
            if (newCondition == BookCopyCondition.DAMAGED) {
                bookCopy.setStatus(BookCopyStatus.DAMAGED);
            } else {
                bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            }
        }

        bookCopyRepository.save(bookCopy);
    }

    private TransactionDetail createTransactionDetail(String transactionId, int penaltyFee, String description) {
        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.setTransactionId(transactionId);
        transactionDetail.setPenaltyFee(penaltyFee);
        transactionDetail.setDescription(description);

        return transactionDetailRepository.save(transactionDetail);
    }

    /**
     * Helper method to enhance transaction DTO with username, book title, and book price
     */
    private void enhanceTransactionDto(ResponseTransactionDto responseTransactionDto, Transaction transaction) {
        // Get username
        if (transaction.getUser() != null) {
            responseTransactionDto.setUserName(transaction.getUser().getName());
        } else {
            // Fallback if user relationship is not loaded
            userRepository.findById(transaction.getUserId())
                    .ifPresent(user -> responseTransactionDto.setUserName(user.getName()));
        }

        // Get book title and price
        if (transaction.getBookCopy() != null && transaction.getBookCopy().getBookTitle() != null) {
            responseTransactionDto.setBookTitle(transaction.getBookCopy().getBookTitle().getTitle());
            responseTransactionDto.setBookPrice(transaction.getBookCopy().getBookTitle().getPrice());
        } else {
            // Fallback if relationships are not loaded
            bookCopyRepository.findById(transaction.getBookCopyId())
                    .ifPresent(bookCopy -> {
                        if (bookCopy.getBookTitle() != null) {
                            responseTransactionDto.setBookTitle(bookCopy.getBookTitle().getTitle());
                            responseTransactionDto.setBookPrice(bookCopy.getBookTitle().getPrice());
                        }
                    });
        }
    }
    
    /**
     * Calculate automatic late fee with cap at book price
     * @param transaction The borrowing transaction
     * @param returnDate The actual return date
     * @param bookPrice The book's price (used as maximum penalty)
     * @return Late fee amount (capped at book price)
     */
    private int calculateLateFee(Transaction transaction, LocalDate returnDate, int bookPrice) {
        LocalDate dueDate = transaction.getDueDate();
        
        if (returnDate.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            int lateFee = (int) (daysLate * 5000); // 5,000 VND per day late
            
            // Cap at book replacement cost
            return Math.min(lateFee, bookPrice);
        }
        
        return 0; // No late fee if returned on time or early
    }
    
    /**
     * Calculate late fee without capping (for lost books)
     * @param transaction The transaction
     * @param returnDate The date the book was marked as lost/returned
     * @return Late fee amount (uncapped)
     */    /**
     * Build detailed description of penalty breakdown
     */
    private String buildPenaltyDescription(int lateFee, int additionalFee, String customDescription, boolean isLost, int bookPrice) {
        StringBuilder description = new StringBuilder();
        
        if (isLost) {
            description.append("LOST BOOK: ");
            if (lateFee > 0) {
                long days = lateFee / 5000;
                description.append(String.format("Late fee: %,d VND (%d days × 5,000 VND/day, uncapped). ", lateFee, days));
            }
            description.append(String.format("Book replacement cost: %,d VND. ", bookPrice));
            if (additionalFee > bookPrice) {
                int damageOnly = additionalFee - bookPrice;
                if (damageOnly > 0) {
                    description.append(String.format("Additional damage fee: %,d VND. ", damageOnly));
                }
            }
        } else {
            if (lateFee > 0) {
                long days = lateFee / 5000; // Calculate days from fee
                description.append(String.format("Late fee: %,d VND (%d days × 5,000 VND/day). ", lateFee, days));
            }
            if (additionalFee > 0) {
                description.append(String.format("Additional penalty: %,d VND. ", additionalFee));
            }
        }
        
        if (customDescription != null && !customDescription.trim().isEmpty()) {
            description.append(customDescription.trim());
        }
        
        return description.toString().trim();
    }

    /**
     * Build return message with penalty breakdown
     */
    private String buildReturnMessage(int lateFee, int additionalFee, int refundAmount, int bookPrice) {
        StringBuilder message = new StringBuilder("Book returned successfully. ");
        
        int totalPenalties = lateFee + additionalFee;
        
        if (lateFee > 0) {
            long days = lateFee / 5000;
            message.append(String.format("Late fee: %,d VND (%d days). ", lateFee, days));
        }
        if (additionalFee > 0) {
            message.append(String.format("Additional penalty: %,d VND. ", additionalFee));
        }
        if (totalPenalties > 0) {
            message.append(String.format("Total penalties: %,d VND. ", totalPenalties));
        }
        
        message.append(String.format("Refund amount: %,d VND.", refundAmount));
        
        return message.toString();
    }
}
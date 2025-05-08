package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.TransactionDetailRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dto.DamagedBookCopyDto;
import com.todo.backend.dto.TransactionCreateDto;
import com.todo.backend.dto.TransactionUpdateDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.TransactionDetail;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final BookCopyRepository bookCopyRepository;

    public TransactionService(TransactionRepository transactionRepository, TransactionDetailRepository transactionDetailRepository, BookCopyRepository bookCopyRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    public Transaction createTransaction(TransactionCreateDto transactionCreateDto) {
        Transaction transaction = transactionCreateDto.getTransaction();
        List<String> bookCopyIds = transactionCreateDto.getBookCopyIds();
        List<TransactionDetail> unreturnedDetails = transactionDetailRepository.findByUserIdAndNotReturned(transaction.getUserId());

        if (transactionRepository.existsById(transaction.getId())) {
            throw new RuntimeException("Transaction with ID already exists");
        }

        if (bookCopyIds.isEmpty()) {
            throw new RuntimeException("Transaction must have at least one bookCopy");
        }

        if (bookCopyIds.size() > 5 || unreturnedDetails.size() + bookCopyIds.size() > 5) {
            throw new RuntimeException("User can only borrow 5 books at a time");
        }

        // Check if all books are available
        for (String bookCopyId : bookCopyIds) {
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));

            if (bookCopy == null || !bookCopy.getStatus().equals("AVAILABLE")) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not available");
            }

            bookCopy.setStatus("BORROWED");
        }

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
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(TransactionUpdateDto transactionUpdateDto) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = currentDate.format(formatter);

        Transaction existingTransaction = transactionRepository.findById(transactionUpdateDto.getTransaction().getId())
                .orElseThrow(() -> new RuntimeException("Transaction with ID not found"));

        Transaction transaction = transactionUpdateDto.getTransaction();
        List<String> returnedBookCopies = transactionUpdateDto.getReturnedBookCopies();
        List<DamagedBookCopyDto> damagedBookCopies = transactionUpdateDto.getDamagedBookCopies();
        List<String> removedBookCopies = transactionUpdateDto.getRemovedBookCopies();
        List<String> borrowedBookCopies = new ArrayList<>(transactionDetailRepository.findByTransactionId(transaction.getId())
                .stream()
                .map(TransactionDetail::getBookCopyId)
                .toList());

        // Check if any duplicate book copies
        List<String> allBookCopies = new ArrayList<>();
        allBookCopies.addAll(returnedBookCopies);
        allBookCopies.addAll(damagedBookCopies.stream().map(DamagedBookCopyDto::getBookCopyId).toList());
        allBookCopies.addAll(removedBookCopies);

        if (allBookCopies.size() != allBookCopies.stream().distinct().count()) {
            throw new RuntimeException("Duplicate book copies found in the update request");
        }

        List<TransactionDetail> details = new ArrayList<>();

        // Update returned book copies
        for (String bookCopyId : returnedBookCopies) {
            if (!borrowedBookCopies.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            borrowedBookCopies.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("AVAILABLE");

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setTransactionId(transaction.getId());
            transactionDetail.setBookCopyId(bookCopyId);
            transactionDetail.setReturnedDate(today);
            transactionDetail.setPenaltyFee(0);
            details.add(transactionDetail);
        }

        // Update damaged book copies
        for (DamagedBookCopyDto damagedBookCopy : damagedBookCopies) {
            String bookCopyId = damagedBookCopy.getBookCopyId();
            if (!borrowedBookCopies.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            borrowedBookCopies.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("DAMAGED");

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setTransactionId(transaction.getId());
            transactionDetail.setBookCopyId(bookCopyId);
            transactionDetail.setReturnedDate(today);
            transactionDetail.setPenaltyFee(damagedBookCopy.getPenaltyFee());
            details.add(transactionDetail);
        }

        // Update removed book copies
        for (String bookCopyId : removedBookCopies) {
            if (!borrowedBookCopies.contains(bookCopyId)) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " is not part of this transaction");
            }

            // Only remove if the book copy is returned
            TransactionDetail transactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(transaction.getId(), bookCopyId);
            if (transactionDetail.getReturnedDate() == null) {
                throw new RuntimeException("BookCopy with ID " + bookCopyId + " must be returned before removal");
            }

            borrowedBookCopies.remove(bookCopyId);
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("BookCopy with ID " + bookCopyId + " not found"));
            bookCopy.setStatus("AVAILABLE");
        }

        // Keep remaining transaction details
        for (String bookCopyId : borrowedBookCopies) {
            TransactionDetail transactionDetail = transactionDetailRepository.findByTransactionIdAndBookCopyId(transaction.getId(), bookCopyId);
            if (transactionDetail != null) {
                details.add(transactionDetail);
            }
        }

        // Never allow removing all book copies in a transaction
        if (details.isEmpty()) {
            throw new RuntimeException("Transaction must have at least one bookCopy");
        }

        // Update transaction details
        existingTransaction.getTransactionDetails().clear();
        existingTransaction.getTransactionDetails().addAll(details);
        return transactionRepository.save(existingTransaction);
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

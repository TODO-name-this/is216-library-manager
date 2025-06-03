package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.Reservation;
import com.todo.backend.entity.Transaction;
import com.todo.backend.entity.User;
import com.todo.backend.mapper.BookCopyMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BookCopyService {    private final BookCopyRepository bookCopyRepository;
    private final BookTitleRepository bookTitleRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookCopyMapper bookCopyMapper;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookTitleRepository bookTitleRepository, 
                          ReservationRepository reservationRepository, TransactionRepository transactionRepository, 
                          UserRepository userRepository, BookCopyMapper bookCopyMapper) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookTitleRepository = bookTitleRepository;
        this.reservationRepository = reservationRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bookCopyMapper = bookCopyMapper;
    }

    public List<ResponseBookCopyDto> getAllBookCopies() {
        List<BookCopy> bookCopies = bookCopyRepository.findAll();
        return bookCopies.stream()
                .map(this::buildEnhancedBookCopyDto)
                .toList();
    }

    public ResponseBookCopyDto getBookCopyDto(String id) {
        BookCopy bookCopy = getBookCopy(id);
        return buildEnhancedBookCopyDto(bookCopy);
    }

    public BookCopy getBookCopy(String id) {
        return bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));
    }

    private ResponseBookCopyDto buildEnhancedBookCopyDto(BookCopy bookCopy) {
        ResponseBookCopyDto.ResponseBookCopyDtoBuilder builder = ResponseBookCopyDto.builder()
                .id(bookCopy.getId())
                .bookTitleId(bookCopy.getBookTitleId())
                .status(bookCopy.getStatus())
                .condition(bookCopy.getCondition()); // Add the condition field

        // Add book title information
        if (bookCopy.getBookTitle() != null) {
            builder.bookTitle(bookCopy.getBookTitle().getTitle())
                   .bookPhotoUrl(bookCopy.getBookTitle().getImageUrl())
                   .bookPrice(bookCopy.getBookTitle().getPrice());
        }

        // Add borrower information if the book is currently borrowed
        if ("BORROWED".equals(bookCopy.getStatus())) {
            // Find the current borrower through transactions
            List<Transaction> unreturned = transactionRepository
                    .findByBookCopyIdAndReturnedDateIsNull(bookCopy.getId());

            if (!unreturned.isEmpty()) {
                Transaction currentTransaction = unreturned.get(0);
                User borrower = userRepository.findById(currentTransaction.getUserId()).orElse(null);
                
                if (borrower != null) {
                    builder.borrowerCccd(borrower.getCccd())
                           .borrowerName(borrower.getName())
                           .borrowerId(borrower.getId());
                }
            }
        }

        // Add reserver information if the book is currently reserved
        if ("RESERVED".equals(bookCopy.getStatus())) {
            // Find the current reserver through reservations (any reservation with this bookCopyId assigned)
            List<Reservation> activeReservations = reservationRepository
                    .findByBookCopyId(bookCopy.getId());

            if (!activeReservations.isEmpty()) {
                Reservation currentReservation = activeReservations.get(0);
                User reserver = userRepository.findById(currentReservation.getUserId()).orElse(null);
                
                if (reserver != null) {
                    builder.borrowerCccd(reserver.getCccd())
                           .borrowerName(reserver.getName())
                           .borrowerId(reserver.getId());
                }
            }
        }

        return builder.build();
    }

    public ResponseBookCopyDto createBookCopy(BookCopyDto bookCopyDto) {
        if (!bookTitleRepository.existsById(bookCopyDto.getBookTitleId())) {
            throw new IllegalArgumentException("BookTitle with this ID does not exist");
        }

        // Create a single book copy since we're returning individual copies now
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBookTitleId(bookCopyDto.getBookTitleId());
        bookCopy.setStatus("AVAILABLE");
        bookCopy.setCondition("NEW"); // Set default condition for new book copies or you could have bookCopy condition in the dto idk
        bookCopyRepository.save(bookCopy);

        // Return the enhanced DTO for the created copy
        return buildEnhancedBookCopyDto(bookCopy);
    }

    public void deleteBookCopy(String id) {
        BookCopy existingBookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));

        if (!existingBookCopy.getStatus().equals("AVAILABLE")) {
            throw new IllegalArgumentException("Cannot delete a book copy that is not available");
        }

        bookCopyRepository.delete(existingBookCopy);
    }
}

package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.TransactionRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.bookcopy.CreateBookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.entity.*;
import com.todo.backend.mapper.BookCopyMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookTitleRepository bookTitleRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookCopyMapper bookCopyMapper;

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
        if (BookCopyStatus.BORROWED.equals(bookCopy.getStatus())) {
            // Find the current borrower through transactions
            List<Transaction> unreturned = transactionRepository
                    .findByBookCopyIdAndReturnedDateIsNull(bookCopy.getId());

            if (!unreturned.isEmpty()) {
                Transaction currentTransaction = unreturned.getFirst();
                userRepository.findById(currentTransaction.getUserId())
                        .ifPresent(borrower -> builder.borrowerCccd(borrower.getCccd())
                        .borrowerName(borrower.getName())
                        .borrowerId(borrower.getId()));
            }
        }

        return builder.build();
    }

    public List<ResponseBookCopyDto> createBookCopies(CreateBookCopyDto bookCopyDto) {
        if (!bookTitleRepository.existsById(bookCopyDto.getBookTitleId())) {
            throw new IllegalArgumentException("BookTitle with this ID does not exist");
        }

        // Create a new BookCopy entity for each copy requested
        var copies = new ArrayList<ResponseBookCopyDto>();
        for (int i = 0; i < bookCopyDto.getQuantity(); i++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBookTitleId(bookCopyDto.getBookTitleId());
            var condition = bookCopyDto.getCondition();
            if (condition == null) {
                condition = BookCopyCondition.NEW; // Default condition if not provided
            }
            bookCopy.setStatus(BookCopyStatus.AVAILABLE);
            bookCopy.setCondition(condition); // Set default condition for new book copies or you could have bookCopy condition in the dto idk
            bookCopyRepository.save(bookCopy);
            copies.add(buildEnhancedBookCopyDto(bookCopy));
        }

        // Return the enhanced DTO for the created copy
        return copies;
    }

    public void deleteBookCopy(String id) {
        BookCopy existingBookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));

        bookCopyRepository.delete(existingBookCopy);
    }

    public ResponseBookCopyDto updateBookCopy(String id, CreateBookCopyDto bookCopyDto) {
        BookCopy existingBookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));

        // Update the fields of the existing book copy
        // TODO
//        bookCopyMapper.updateEntityFromDto(bookCopyDto, existingBookCopy);
        bookCopyRepository.save(existingBookCopy);

        throw new UnsupportedOperationException("Update operation is not implemented yet");
//        return buildEnhancedBookCopyDto(existingBookCopy);
    }
}

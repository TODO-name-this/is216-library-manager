package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dto.bookcopy.BookCopyDto;
import com.todo.backend.dto.bookcopy.ResponseBookCopyDto;
import com.todo.backend.entity.BookCopy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Transactional
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookTitleRepository bookTitleRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookTitleRepository bookTitleRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookTitleRepository = bookTitleRepository;
    }

    public BookCopy getBookCopy(String id) {
        return bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));
    }

    public ResponseBookCopyDto createBookCopy(BookCopyDto bookCopyDto) {
        ResponseBookCopyDto dto = ResponseBookCopyDto.builder().bookCopyIds(new ArrayList<>()).build();

        if (!bookTitleRepository.existsById(bookCopyDto.getBookTitleId())) {
            throw new IllegalArgumentException("BookTitle with this ID does not exist");
        }

        for (int i = 0; i < bookCopyDto.getQuantity(); i++) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setBookTitleId(bookCopyDto.getBookTitleId());
            bookCopy.setStatus("AVAILABLE");
            bookCopyRepository.save(bookCopy);

            dto.getBookCopyIds().add(bookCopy.getId());
        }

        return dto;
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

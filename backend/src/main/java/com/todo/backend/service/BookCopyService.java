package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.entity.BookCopy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
    }

    public BookCopy createBookCopy(BookCopy bookCopy) {
        if (bookCopyRepository.existsById(bookCopy.getId())) {
            throw new IllegalArgumentException("BookCopy with this ID already exists");
        }

        return bookCopyRepository.save(bookCopy);
    }

    public BookCopy updateBookCopy(BookCopy bookCopy) {
        if (!bookCopyRepository.existsById(bookCopy.getId())) {
            throw new IllegalArgumentException("BookCopy with this ID does not exist");
        }

        return bookCopyRepository.save(bookCopy);
    }

    public void deleteBookCopy(String id) {
        BookCopy existingBookCopy = bookCopyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BookCopy with this ID does not exist"));

        if (existingBookCopy.getStatus().equals("BORROWED")) {
            throw new IllegalArgumentException("Cannot delete a borrowed book copy");
        }

        bookCopyRepository.delete(existingBookCopy);
    }
}

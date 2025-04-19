package com.todo.backend.service;

import com.todo.backend.dao.AuthorRepository;
import com.todo.backend.dao.BookRepository;
import com.todo.backend.entity.Book;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;


    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public Book createBook(Book book) {
        if (bookRepository.existsById(book.getId())) {
            throw new IllegalArgumentException("Book with ID already exists: " + book.getId());
        }
        return bookRepository.save(book);
    }

    public Book updateBook(String id, Book updatedBook) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));

        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }

        if (updatedBook.getIsbn() != null) {
            existingBook.setIsbn(updatedBook.getIsbn());
        }

        if (updatedBook.getImageUrl() != null) {
            existingBook.setImageUrl(updatedBook.getImageUrl());
        }

        if (updatedBook.getPublishedDate() != null) {
            existingBook.setPublishedDate(updatedBook.getPublishedDate());
        }

        return bookRepository.save(existingBook);
    }

    public void deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));
        if (book.getTransactions() != null && !book.getTransactions().isEmpty()) {
            throw new IllegalStateException("Cannot delete book with existing transactions.");
        }

        if (book.getReservations() != null && !book.getReservations().isEmpty()) {
            throw new IllegalStateException("Cannot delete book with active reservations.");
        }
        bookRepository.deleteById(id);
    }

}

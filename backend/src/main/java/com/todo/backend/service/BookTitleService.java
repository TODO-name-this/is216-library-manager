package com.todo.backend.service;

import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.entity.BookAuthor;
import com.todo.backend.entity.BookCategory;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookTitleService {
    private final BookTitleRepository bookTitleRepository;

    public BookTitleService(BookTitleRepository bookTitleRepository) {
        this.bookTitleRepository = bookTitleRepository;
    }

    public BookTitle createBookTitle(BookTitleDto bookTitleDto) {
        if (bookTitleRepository.existsById(bookTitleDto.getBookTitle().getId())) {
            throw new RuntimeException("Book title ID already exists");
        }

        if (bookTitleDto.getAuthorIds().size() != bookTitleDto.getAuthorIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate author IDs found in the request");
        }

        if (bookTitleDto.getCategoryIds().size() != bookTitleDto.getCategoryIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate category IDs found in the request");
        }

        BookTitle bookTitle = bookTitleDto.getBookTitle();
        List<BookAuthor> bookAuthors = new ArrayList<>();
        List<BookCategory> bookCategories = new ArrayList<>();

        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(bookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            bookAuthors.add(bookAuthor);
        }

        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(bookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            bookCategories.add(bookCategory);
        }

        bookTitle.setBookAuthors(bookAuthors);
        bookTitle.setBookCategories(bookCategories);

        return bookTitleRepository.save(bookTitle);
    }

    public BookTitle updateBookTitle(BookTitleDto bookTitleDto) {
        BookTitle existingBookTitle = bookTitleRepository.findById(bookTitleDto.getBookTitle().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        existingBookTitle.getBookAuthors().clear();
        existingBookTitle.getBookCategories().clear();

        BookTitle bookTitle = bookTitleDto.getBookTitle();

        existingBookTitle.setImageUrl(bookTitle.getImageUrl());
        existingBookTitle.setTitle(bookTitle.getTitle());
        existingBookTitle.setIsbn(bookTitle.getIsbn());
        existingBookTitle.setPublishedDate(bookTitle.getPublishedDate());
        existingBookTitle.setPublisherId(bookTitle.getPublisherId());

        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(bookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            existingBookTitle.getBookAuthors().add(bookAuthor);
        }

        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(bookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            existingBookTitle.getBookCategories().add(bookCategory);
        }

        return bookTitleRepository.save(existingBookTitle);
    }

    public void deleteBookTitle(String id) {
        BookTitle existingBookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        List<BookCopy> bookCopies = existingBookTitle.getBookCopies();
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getStatus().equals("BORROWED") || bookCopy.getStatus().equals("RESERVED")) {
                throw new IllegalArgumentException("Cannot delete book title with borrowed or reserved copies");
            }
        }

        bookTitleRepository.delete(existingBookTitle);
    }
}

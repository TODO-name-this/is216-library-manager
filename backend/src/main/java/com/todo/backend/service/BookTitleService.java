package com.todo.backend.service;

import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dto.booktitle.BookTitleDto;
import com.todo.backend.dto.booktitle.ResponseBookTitleDto;
import com.todo.backend.dto.review.ResponseReviewDto;
import com.todo.backend.entity.BookAuthor;
import com.todo.backend.entity.BookCategory;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.mapper.BookTitleMapper;
import com.todo.backend.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookTitleService {
    private final BookTitleRepository bookTitleRepository;
    private final BookTitleMapper bookTitleMapper;
    private final ReviewMapper reviewMapper;

    public BookTitleService(BookTitleRepository bookTitleRepository, BookTitleMapper bookTitleMapper, ReviewMapper reviewMapper) {
        this.bookTitleRepository = bookTitleRepository;
        this.bookTitleMapper = bookTitleMapper;
        this.reviewMapper = reviewMapper;
    }    public List<ResponseBookTitleDto> getAllBookTitles() {
        List<BookTitle> bookTitles = bookTitleRepository.findAll();
        List<ResponseBookTitleDto> responseBookTitleDtos = new ArrayList<>();

        for (BookTitle bookTitle : bookTitles) {
            ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
            List<String> authorIds = new ArrayList<>(bookTitle.getBookAuthors().stream().map(BookAuthor::getAuthorId).toList());
            List<String> categoryIds = new ArrayList<>(bookTitle.getBookCategories().stream().map(BookCategory::getCategoryId).toList());

            responseBookTitleDto.setAuthorIds(authorIds);
            responseBookTitleDto.setCategoryIds(categoryIds);
            responseBookTitleDtos.add(responseBookTitleDto);
        }        return responseBookTitleDtos;
    }

    public List<ResponseBookTitleDto> getAllBookTitlesWithCategoryAndAuthorName() {
        List<BookTitle> bookTitles = bookTitleRepository.findAll();
        List<ResponseBookTitleDto> responseBookTitleDtos = new ArrayList<>();

        for (BookTitle bookTitle : bookTitles) {
            ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
            List<String> authorNames = new ArrayList<>(bookTitle.getBookAuthors().stream()
                    .map(bookAuthor -> bookAuthor.getAuthor().getName()).toList());
            List<String> categoryNames = new ArrayList<>(bookTitle.getBookCategories().stream()
                    .map(bookCategory -> bookCategory.getCategory().getName()).toList());

            responseBookTitleDto.setAuthorNames(authorNames);
            responseBookTitleDto.setCategoryNames(categoryNames);
            responseBookTitleDtos.add(responseBookTitleDto);
        }

        return responseBookTitleDtos;
    }

    public ResponseBookTitleDto getBookTitle(String id) {
        BookTitle bookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
        
        // Set author and category IDs
        List<String> authorIds = new ArrayList<>(bookTitle.getBookAuthors().stream().map(BookAuthor::getAuthorId).toList());
        List<String> categoryIds = new ArrayList<>(bookTitle.getBookCategories().stream().map(BookCategory::getCategoryId).toList());
        responseBookTitleDto.setAuthorIds(authorIds);
        responseBookTitleDto.setCategoryIds(categoryIds);
        
        // Set author and category names
        List<String> authorNames = new ArrayList<>(bookTitle.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName()).toList());
        List<String> categoryNames = new ArrayList<>(bookTitle.getBookCategories().stream()
                .map(bookCategory -> bookCategory.getCategory().getName()).toList());
        responseBookTitleDto.setAuthorNames(authorNames);
        responseBookTitleDto.setCategoryNames(categoryNames);
        
        // Set reviews
        List<ResponseReviewDto> reviews = bookTitle.getReviews().stream()
                .map(reviewMapper::toResponseDto)
                .toList();
        responseBookTitleDto.setReviews(reviews);

        return responseBookTitleDto;
    }

    public ResponseBookTitleDto createBookTitle(BookTitleDto bookTitleDto) {
        if (bookTitleDto.getAuthorIds().size() != bookTitleDto.getAuthorIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate author IDs found in the request");
        }

        if (bookTitleDto.getCategoryIds().size() != bookTitleDto.getCategoryIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate category IDs found in the request");
        }

        BookTitle bookTitle = bookTitleMapper.toEntity(bookTitleDto);
        if (bookTitleRepository.existsByIsbn(bookTitle.getIsbn())) {
            throw new RuntimeException("Book title with this ISBN already exists");
        }

        bookTitleRepository.saveAndFlush(bookTitle);

        List<BookAuthor> bookAuthors = new ArrayList<>();
        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(bookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            bookAuthors.add(bookAuthor);
        }

        List<BookCategory> bookCategories = new ArrayList<>();
        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(bookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            bookCategories.add(bookCategory);
        }

        bookTitle.setBookAuthors(bookAuthors);
        bookTitle.setBookCategories(bookCategories);

        bookTitleRepository.save(bookTitle);

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(bookTitle);
        responseBookTitleDto.setAuthorIds(bookTitleDto.getAuthorIds());
        responseBookTitleDto.setCategoryIds(bookTitleDto.getCategoryIds());

        return responseBookTitleDto;
    }

    public ResponseBookTitleDto updateBookTitle(String id, BookTitleDto bookTitleDto) {
        if (bookTitleDto.getAuthorIds().size() != bookTitleDto.getAuthorIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate author IDs found in the request");
        }

        if (bookTitleDto.getCategoryIds().size() != bookTitleDto.getCategoryIds().stream().distinct().count()) {
            throw new RuntimeException("Duplicate category IDs found in the request");
        }

        BookTitle existingBookTitle = bookTitleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book title ID does not exist"));
        existingBookTitle.getBookAuthors().clear();
        existingBookTitle.getBookCategories().clear();

        bookTitleMapper.updateEntityFromDto(bookTitleDto, existingBookTitle);

        for (String authorId : bookTitleDto.getAuthorIds()) {
            BookAuthor bookAuthor = new BookAuthor();
            bookAuthor.setBookTitleId(existingBookTitle.getId());
            bookAuthor.setAuthorId(authorId);
            existingBookTitle.getBookAuthors().add(bookAuthor);
        }

        for (String categoryId : bookTitleDto.getCategoryIds()) {
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBookTitleId(existingBookTitle.getId());
            bookCategory.setCategoryId(categoryId);
            existingBookTitle.getBookCategories().add(bookCategory);
        }

        bookTitleRepository.save(existingBookTitle);

        ResponseBookTitleDto responseBookTitleDto = bookTitleMapper.toResponseDto(existingBookTitle);
        responseBookTitleDto.setAuthorIds(bookTitleDto.getAuthorIds());
        responseBookTitleDto.setCategoryIds(bookTitleDto.getCategoryIds());

        return responseBookTitleDto;
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

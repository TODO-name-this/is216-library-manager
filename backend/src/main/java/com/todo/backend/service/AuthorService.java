package com.todo.backend.service;

import com.todo.backend.dao.AuthorRepository;
import com.todo.backend.dto.author.AuthorDto;
import com.todo.backend.dto.author.ResponseAuthorDto;
import com.todo.backend.entity.Author;
import com.todo.backend.entity.BookAuthor;
import com.todo.backend.mapper.AuthorMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public List<ResponseAuthorDto> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authorMapper.toResponseDtoList(authors);
    }

    public ResponseAuthorDto getAuthor(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author with this ID does not exist"));        ResponseAuthorDto responseAuthorDto = authorMapper.toResponseDto(author);
        
        // Set book IDs, book names, book URLs, publisher IDs, and publisher names
        List<String> bookIds = new ArrayList<>(author.getBookAuthors().stream()
                .map(BookAuthor::getBookTitleId).toList());
        List<String> bookNames = new ArrayList<>(author.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getBookTitle().getTitle()).toList());
        List<String> bookUrls = new ArrayList<>(author.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getBookTitle().getImageUrl()).toList());
        List<String> publisherIds = new ArrayList<>(author.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getBookTitle().getPublisherId()).toList());
        List<String> publisherNames = new ArrayList<>(author.getBookAuthors().stream()
                .map(bookAuthor -> bookAuthor.getBookTitle().getPublisher().getName()).toList());
        
        responseAuthorDto.setBookIds(bookIds);
        responseAuthorDto.setBookNames(bookNames);
        responseAuthorDto.setBookUrls(bookUrls);
        responseAuthorDto.setPublisherIds(publisherIds);
        responseAuthorDto.setPublisherNames(publisherNames);

        return responseAuthorDto;
    }

    public ResponseAuthorDto createAuthor(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        authorRepository.save(author);

        return authorMapper.toResponseDto(author);
    }

    public ResponseAuthorDto updateAuthor(String id, AuthorDto authorDto) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author with this ID does not exist"));

        authorMapper.updateEntityFromDto(authorDto, existingAuthor);

        authorRepository.save(existingAuthor);

        return authorMapper.toResponseDto(existingAuthor);
    }

    public void deleteAuthor(String id) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author with this ID does not exist"));

        if (existingAuthor.getBookAuthors() != null && !existingAuthor.getBookAuthors().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete author with associated book titles");
        }

        authorRepository.delete(existingAuthor);
    }
}

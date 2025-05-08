package com.todo.backend.service;

import com.todo.backend.dao.AuthorRepository;
import com.todo.backend.dto.author.AuthorDto;
import com.todo.backend.dto.author.ResponseAuthorDto;
import com.todo.backend.entity.Author;
import com.todo.backend.entity.BookAuthor;
import com.todo.backend.mapper.AuthorMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    public ResponseAuthorDto getAuthor(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author with this ID does not exist"));

        return authorMapper.toResponseDto(author);
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

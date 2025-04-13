package com.todo.backend.service;

import com.todo.backend.dao.AuthorRepository;
import com.todo.backend.entity.Author;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author createAuthor(Author author) {
        if (authorRepository.existsById(author.getId())) {
            throw new IllegalArgumentException("Author already exists with ID: " + author.getId());
        }
        return authorRepository.save(author);
    }

    public Author updateAuthor(String id, Author updatedAuthor) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));

        if (updatedAuthor.getName() != null) {
            existingAuthor.setName(updatedAuthor.getName());
        }

        if (updatedAuthor.getAvatarUrl() != null) {
            existingAuthor.setAvatarUrl(updatedAuthor.getAvatarUrl());
        }

        if (updatedAuthor.getBiography() != null) {
            existingAuthor.setBiography(updatedAuthor.getBiography());
        }

        if (updatedAuthor.getBirthday() != null) {
            existingAuthor.setBirthday(updatedAuthor.getBirthday());
        }

        return authorRepository.save(existingAuthor);
    }

    public void deleteAuthor(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with ID: " + id));
        authorRepository.delete(author);
    }
}

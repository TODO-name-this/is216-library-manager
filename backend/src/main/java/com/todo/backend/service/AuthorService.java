package com.todo.backend.service;

import com.todo.backend.dao.AuthorRepository;
import com.todo.backend.entity.Author;
import com.todo.backend.entity.BookAuthor;
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
            throw new IllegalArgumentException("Author with this ID already exists");
        }

        return authorRepository.save(author);
    }

    public Author updateAuthor(Author author) {
        if (!authorRepository.existsById(author.getId())) {
            throw new IllegalArgumentException("Author with this ID does not exist");
        }

        return authorRepository.save(author);
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

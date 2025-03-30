package com.todo.backend.entity.compositekey;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class BookAuthorPrimaryKey implements Serializable {
    private String bookId;
    private String authorId;

    public BookAuthorPrimaryKey(String bookId, String authorId) {
        this.bookId = bookId;
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof BookAuthorPrimaryKey other) {
            return (this.bookId != null && this.bookId.equals(other.bookId)) &&
                   (this.authorId != null && this.authorId.equals(other.authorId));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, authorId);
    }
}

package com.todo.backend.entity.compositekey;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class BookAuthorPrimaryKey implements Serializable {
    private String bookTitleId;
    private String authorId;

    public BookAuthorPrimaryKey(String bookTitleId, String authorId) {
        this.bookTitleId = bookTitleId;
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof BookAuthorPrimaryKey other) {
            return (this.bookTitleId != null && this.bookTitleId.equals(other.bookTitleId)) &&
                   (this.authorId != null && this.authorId.equals(other.authorId));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookTitleId, authorId);
    }
}

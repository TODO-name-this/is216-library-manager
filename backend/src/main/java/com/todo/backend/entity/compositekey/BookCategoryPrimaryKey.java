package com.todo.backend.entity.compositekey;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class BookCategoryPrimaryKey implements Serializable {
    private String bookId;
    private String categoryId;

    private BookCategoryPrimaryKey(String bookId, String categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof BookCategoryPrimaryKey other) {
            return (this.bookId != null && this.bookId.equals(other.bookId)) &&
                   (this.categoryId != null && this.categoryId.equals(other.categoryId));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, categoryId);
    }
}

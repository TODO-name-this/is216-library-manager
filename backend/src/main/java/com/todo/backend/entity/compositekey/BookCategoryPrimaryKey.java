package com.todo.backend.entity.compositekey;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class BookCategoryPrimaryKey implements Serializable {
    private String bookTitleId;
    private String categoryId;

    private BookCategoryPrimaryKey(String bookTitleId, String categoryId) {
        this.bookTitleId = bookTitleId;
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof BookCategoryPrimaryKey other) {
            return (this.bookTitleId != null && this.bookTitleId.equals(other.bookTitleId)) &&
                   (this.categoryId != null && this.categoryId.equals(other.categoryId));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookTitleId, categoryId);
    }
}

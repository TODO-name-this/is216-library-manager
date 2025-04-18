package com.todo.backend.entity.compositekey;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class TransactionDetailPrimaryKey implements Serializable {
    private String transactionId;
    private String bookCopyId;

    public TransactionDetailPrimaryKey(String transactionId, String bookCopyId) {
        this.transactionId = transactionId;
        this.bookCopyId = bookCopyId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof TransactionDetailPrimaryKey other) {
            return (this.transactionId != null && this.transactionId.equals(other.transactionId)) &&
                   (this.bookCopyId != null && this.bookCopyId.equals(other.bookCopyId));
        }

        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(transactionId, bookCopyId); }
}

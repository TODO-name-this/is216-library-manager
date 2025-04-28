package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todo.backend.entity.compositekey.TransactionDetailPrimaryKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "TRANSACTION_DETAIL")
@Data
@IdClass(TransactionDetailPrimaryKey.class)
public class TransactionDetail {
    @Id
    @NotNull
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Id
    @NotNull
    @Column(name = "BOOK_COPY_ID")
    private String bookCopyId;

    @Column(name = "RETURNED_DATE")
    private String returnedDate;

    @Column(name = "PENALTY_FEE")
    private int penaltyFee;

    // Relationship with Transaction
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Transaction transaction;

    // Relationship with BookCopy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_COPY_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private BookCopy bookCopy;
}

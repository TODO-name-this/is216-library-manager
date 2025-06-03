package com.todo.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;



@Entity
@Table(name = "TRANSACTION_DETAIL")
@Data
public class TransactionDetail {
    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "PENALTY_FEE")
    private int penaltyFee;

    @Column(name = "DESCRIPTION")
    private String description;

    // Relationship with Transaction
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Transaction transaction;
}

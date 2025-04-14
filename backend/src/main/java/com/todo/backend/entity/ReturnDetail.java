package com.todo.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "RETURN_DETAIL")
@Data
public class ReturnDetail {
    @Id
    @Column(name = "ID")
    @NotNull
    private String id;

    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "DAMAGED_QUANTITY")
    private int damagedQuantity;

    @Column(name = "PENALTY_FEE")
    private int penaltyFee;

    // Relationships with Transaction
    @OneToOne
    @MapsId
    @JoinColumn(name = "TRANSACTION_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    private Transaction transaction;
}

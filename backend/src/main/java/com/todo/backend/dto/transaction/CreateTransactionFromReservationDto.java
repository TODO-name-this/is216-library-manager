package com.todo.backend.dto.transaction;

public class CreateTransactionFromReservationDto {
    private String reservationId;
    private String bookCopyId;

    public String getReservationId() {
        return reservationId;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }
}
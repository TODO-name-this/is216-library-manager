package com.todo.backend.dao;

import com.todo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Reservation findBookByUserIdAndBookId(String userId, String bookId);
    List<Reservation> findBooksByUserId(String userId);
}
package com.todo.backend.dao;

import com.todo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
//    Reservation findBookByUserIdAndBookId(String userId, String bookId);
//    List<Reservation> findBooksByUserId(String userId);

    Reservation findByUserId(@RequestParam("userId") String userId);
    Reservation findByBookTitleIdAndUserId(@RequestParam("bookTitleId") String bookTitleId, @RequestParam("userId") String userId);

}
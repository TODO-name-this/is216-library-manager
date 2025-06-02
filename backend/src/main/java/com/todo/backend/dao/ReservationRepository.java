package com.todo.backend.dao;

import com.todo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ReservationRepository extends JpaRepository<Reservation, String> {
//    Reservation findBookByUserIdAndBookId(String userId, String bookId);
//    List<Reservation> findBooksByUserId(String userId);

    List<Reservation> findByUserId(@RequestParam("userId") String userId);
    List<Reservation> findByUserIdAndStatus(@RequestParam("userId") String userId, @RequestParam("status") String status);
    List<Reservation> findByUserIdAndBookTitleId(@RequestParam("userId") String userId, @RequestParam("bookTitleId") String bookTitleId);
    List<Reservation> findByBookTitleIdAndStatus(@RequestParam("bookTitleId") String bookTitleId, @RequestParam("status") String status);
}
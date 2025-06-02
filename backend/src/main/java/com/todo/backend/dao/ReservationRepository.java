package com.todo.backend.dao;

import com.todo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByUserIdAndStatus(String userId, String status);
    List<Reservation> findByUserIdAndBookTitleId(String userId, String bookTitleId);
    List<Reservation> findByBookTitleIdAndStatus(String bookTitleId, String status);
    List<Reservation> findByBookCopyIdAndStatus(String bookCopyId, String status);
    List<Reservation> findByBookCopyId(String bookCopyId);
}
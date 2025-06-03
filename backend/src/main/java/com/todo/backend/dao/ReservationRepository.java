package com.todo.backend.dao;

import com.todo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByUserId(String userId);
    List<Reservation> findByUserIdAndBookTitleId(String userId, String bookTitleId);
    List<Reservation> findByBookTitleId(String bookTitleId);
    List<Reservation> findByBookCopyId(String bookCopyId);
    
    // Find active reservations (not expired yet)
    @Query("SELECT r FROM Reservation r WHERE r.userId = :userId AND r.expirationDate >= :currentDate")
    List<Reservation> findActiveReservationsByUserId(String userId, LocalDate currentDate);
    
    @Query("SELECT r FROM Reservation r WHERE r.bookTitleId = :bookTitleId AND r.expirationDate >= :currentDate")
    List<Reservation> findActiveReservationsByBookTitleId(String bookTitleId, LocalDate currentDate);
    
    // Find expired reservations for cleanup
    @Query("SELECT r FROM Reservation r WHERE r.expirationDate < :currentDate")
    List<Reservation> findExpiredReservations(LocalDate currentDate);
}
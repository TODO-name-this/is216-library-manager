package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.entity.Reservation;
import com.todo.backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookTitleRepository bookTitleRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, BookCopyRepository bookCopyRepository, BookTitleRepository bookTitleRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.bookTitleRepository = bookTitleRepository;
        this.userRepository = userRepository;
    }

    private void validateReservationRules(Reservation reservation) {
        List<Reservation> pendingReservations = reservationRepository.findByUserIdAndStatus(reservation.getUserId(), "PENDING");

        // Maximum 5 reservations per user
        final int MAX_RESERVATIONS = 5;
        if (pendingReservations.size() >= MAX_RESERVATIONS) {
            throw new RuntimeException("User has reached the maximum number of pending reservations");
        }

        // Only one reservation per book title
        for (Reservation pendingReservation : pendingReservations) {
            if (pendingReservation.getBookTitleId().equals(reservation.getBookTitleId())) {
                throw new RuntimeException("User has already reserved this book");
            }
        }

        // Check if the book title can be borrowed
        BookTitle bookTitle = bookTitleRepository.findById(reservation.getBookTitleId())
                .orElseThrow(() -> new RuntimeException("Book title not found"));

        if (!bookTitle.isCanBorrow()) {
            throw new RuntimeException("This book title cannot be reserved");
        }

        // Check if the deposit is valid
        if (reservation.getDeposit() < 0) {
            throw new RuntimeException("Deposit cannot be negative");
        }
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservationRepository.existsById(reservation.getId())) {
            throw new RuntimeException("Reservation ID already exists");
        }

        // Validate reservation rules
        validateReservationRules(reservation);

        // Check if a user exists
        User user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if a user has enough balances to reserve
        if (user.getBalance() < reservation.getDeposit()) {
            throw new RuntimeException("User does not have enough balance to reserve");
        }

        // Deduct the deposit from the user's balance
        user.setBalance(user.getBalance() - reservation.getDeposit());

        BookCopy availableBookCopy = bookCopyRepository.findFirstByBookTitleIdAndStatus(reservation.getBookTitleId(), "available");
        if (availableBookCopy == null) {
            throw new RuntimeException("No available book copy for reservation");
        }
        reservation.setBookCopyId(availableBookCopy.getId());
        reservation.setStatus("PENDING");

        // Update the status of the book copy to "reserved"
        availableBookCopy.setStatus("RESERVED");
        bookCopyRepository.save(availableBookCopy);

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        Reservation existingReservation = reservationRepository.findById(reservation.getId())
                .orElseThrow(() -> new RuntimeException("Reservation ID does not exist"));

        // If the book title changes, validate and assign a new book copy
        if (!existingReservation.getBookTitleId().equals(reservation.getBookTitleId())) {
            validateReservationRules(reservation);

            if (existingReservation.getStatus().equals("PENDING")) {
                BookCopy oldBookCopy = bookCopyRepository.findById(existingReservation.getBookCopyId())
                        .orElseThrow(() -> new RuntimeException("Book copy not found"));
                oldBookCopy.setStatus("AVAILABLE");
                bookCopyRepository.save(oldBookCopy);
            }

            BookCopy newBookCopy = bookCopyRepository.findFirstByBookTitleIdAndStatus(
                    reservation.getBookTitleId(), "AVAILABLE");
            if (newBookCopy == null) {
                throw new RuntimeException("No available book copy for thís reservation");
            }
            newBookCopy.setStatus("RESERVED");
            bookCopyRepository.save(newBookCopy);

            existingReservation.setBookTitleId(reservation.getBookTitleId());
            existingReservation.setBookCopyId(newBookCopy.getId());
        }

        // Prevent invalid status transition from COMPLETED to PENDING
        if (existingReservation.getStatus().equals("COMPLETED")
                && reservation.getStatus().equals("PENDING")) {
            throw new RuntimeException("Invalid status transition from COMPLETED to PENDING");
        }

        // Return the book copy status and return the deposit if the status changes from PENDING to CANCELLED or COMPLETED
        if (existingReservation.getStatus().equals("PENDING")) {
            if (reservation.getStatus().equals("COMPLETED") || reservation.getStatus().equals("CANCELLED")) {
                BookCopy bookCopy = bookCopyRepository.findById(existingReservation.getBookCopyId())
                        .orElseThrow(() -> new RuntimeException("Book copy not found"));
                bookCopy.setStatus("AVAILABLE");
                bookCopyRepository.save(bookCopy);

                // Return the deposit to the user
                User user = userRepository.findById(existingReservation.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                user.setBalance(user.getBalance() + existingReservation.getDeposit());
                userRepository.save(user);
            }
        }

        // Update the reservation dates if they have changed
        if (!existingReservation.getReservationDate().equals(reservation.getReservationDate())) {
            existingReservation.setReservationDate(reservation.getReservationDate());
        }
        if (!existingReservation.getExpirationDate().equals(reservation.getExpirationDate())) {
            existingReservation.setExpirationDate(reservation.getExpirationDate());
        }

        // Update the status and other properties if needed
        existingReservation.setStatus(reservation.getStatus());

        return reservationRepository.save(existingReservation);
    }

    public void deleteReservation(String id) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (existingReservation.getStatus().equals("PENDING")) {
            // Update the status of the book copy back to "available"
            BookCopy bookCopy = bookCopyRepository.findById(existingReservation.getBookCopyId())
                    .orElseThrow(() -> new RuntimeException("Book copy not found"));
            bookCopy.setStatus("AVAILABLE");
            bookCopyRepository.save(bookCopy);
        }

        reservationRepository.delete(existingReservation);
    }
}

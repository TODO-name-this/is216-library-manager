package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.reservation.ReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.entity.Reservation;
import com.todo.backend.entity.User;
import com.todo.backend.mapper.ReservationMapper;
import com.todo.backend.scheduler.jobs.ReservationExpiryJob;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReservationService {
    private final String RESERVATION_GROUP = "reservationGroup";
    private final String RESERVATION_EXPIRY_JOB_PREFIX = "reservationExpiryJob_";
    private final String RESERVATION_EXPIRY_TRIGGER_PREFIX = "reservationExpiryTrigger_";

    private final ReservationRepository reservationRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookTitleRepository bookTitleRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;
    private final Scheduler scheduler;

    public ReservationService(ReservationRepository reservationRepository, BookCopyRepository bookCopyRepository, BookTitleRepository bookTitleRepository, UserRepository userRepository, ReservationMapper reservationMapper, Scheduler scheduler) {
        this.reservationRepository = reservationRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.bookTitleRepository = bookTitleRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
        this.scheduler = scheduler;
    }

    public ResponseReservationDto getReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        return reservationMapper.toResponseDto(reservation);
    }

    public ResponseReservationDto createReservation(ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.toEntity(reservationDto);

        validateReservationRules(reservation);

        // Check if a user exists
        User user = userRepository.findById(reservationDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if a user has enough balances to reserve
        if (user.getBalance() < reservationDto.getDeposit()) {
            throw new RuntimeException("User does not have enough balance to reserve");
        }

        // Deduct the deposit from the user's balance
        user.setBalance(user.getBalance() - reservationDto.getDeposit());
        userRepository.save(user);

        BookCopy availableBookCopy = bookCopyRepository.findFirstByBookTitleIdAndStatus(reservationDto.getBookTitleId(), "available");
        if (availableBookCopy == null) {
            throw new RuntimeException("No available book copy for reservation");
        }
        reservation.setBookCopyId(availableBookCopy.getId());
        reservation.setStatus("PENDING");

        // Update the status of the book copy to "reserved"
        availableBookCopy.setStatus("RESERVED");
        bookCopyRepository.save(availableBookCopy);

        reservationRepository.save(reservation);

        // Schedule a job to set the reservation as expired after expiration date
        createExpiryJob(reservation);

        return reservationMapper.toResponseDto(reservation);
    }

    public ResponseReservationDto updateReservation(String id, ReservationDto reservationDto) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Prevent updating a reservation that is already completed or canceled
        if (existingReservation.getStatus().equals("COMPLETED") || existingReservation.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Can't update a reservation that is already completed or canceled");
        }

        Reservation updatedReservation = reservationMapper.toEntity(reservationDto);

        // If the book title ID or user_id changes, cancel the old reservation and create a new one
        // Note: Only cancel the reservation if there's a book copy available
        if (!existingReservation.getBookTitleId().equals(updatedReservation.getBookTitleId()) ||
            !existingReservation.getUserId().equals(updatedReservation.getUserId())) {
            // Cancel the old reservation
            finalizeReservation(existingReservation, "CANCELLED");

            // Create a new reservation
            return createReservation(reservationDto);
        }

        // Return the book copy status and return the deposit if the status changes from PENDING to CANCELLED or COMPLETED
        if (existingReservation.getStatus().equals("PENDING")) {
            if (updatedReservation.getStatus().equals("COMPLETED") || updatedReservation.getStatus().equals("CANCELLED")) {
                finalizeReservation(existingReservation, updatedReservation.getStatus());
            }
        }

        // Update the user's balance if the deposit changes
        if (existingReservation.getDeposit() > updatedReservation.getDeposit()) {
            int difference = existingReservation.getDeposit() - updatedReservation.getDeposit();
            User user = userRepository.findById(existingReservation.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setBalance(user.getBalance() + difference);
            userRepository.save(user);
        }
        else if (existingReservation.getDeposit() < updatedReservation.getDeposit()) {
            int difference = updatedReservation.getDeposit() - existingReservation.getDeposit();
            User user = userRepository.findById(existingReservation.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getBalance() < difference) {
                throw new RuntimeException("User does not have enough balance to update the reservation");
            }
            user.setBalance(user.getBalance() - difference);
            userRepository.save(user);
        }

        if (updatedReservation.getReservationDate().isAfter(updatedReservation.getExpirationDate())) {
            throw new RuntimeException("Reservation date cannot be after the expiration date");
        }

        // Update the reservation details
        reservationMapper.updateEntityFromDto(reservationDto, existingReservation);

        reservationRepository.save(existingReservation);

        // Reschedule the job to set the reservation as expired after the new expiration date
        JobKey jobKey = new JobKey(RESERVATION_EXPIRY_JOB_PREFIX + existingReservation.getId(), RESERVATION_GROUP);
        try {
            scheduler.deleteJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Failed to delete old reservation expiry job: ", e);
        }

        createExpiryJob(existingReservation);


        return reservationMapper.toResponseDto(existingReservation);
    }

    public void deleteReservation(String id) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (existingReservation.getStatus().equals("PENDING")) {
            finalizeReservation(existingReservation, "CANCELLED");
        }

        reservationRepository.delete(existingReservation);
    }

    public void setExpiredReservation(String id) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (existingReservation.getStatus().equals("PENDING")) {
            finalizeReservation(existingReservation, "EXPIRED");
        }
    }

    private void createExpiryJob(Reservation reservation) {
        JobDetail jobDetail = JobBuilder.newJob(ReservationExpiryJob.class)
                .withIdentity(RESERVATION_EXPIRY_JOB_PREFIX + reservation.getId(), RESERVATION_GROUP)
                .usingJobData("reservationId", reservation.getId())
                .usingJobData("retryCount", 0)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(RESERVATION_EXPIRY_TRIGGER_PREFIX + reservation.getId(), RESERVATION_GROUP)
                .startAt(Date.from(reservation.getExpirationDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                .forJob(jobDetail)
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule reservation expiry job: ", e);
        }
    }

    private void finalizeReservation(Reservation reservation, String status) {
        // Restore the book copy status to "AVAILABLE"
        BookCopy bookCopy = bookCopyRepository.findById(reservation.getBookCopyId())
                .orElseThrow(() -> new RuntimeException("Book copy not found"));
        bookCopy.setStatus("AVAILABLE");
        bookCopyRepository.save(bookCopy);

        // Return the deposit to the user
        User user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(user.getBalance() + reservation.getDeposit());
        userRepository.save(user);

        reservation.setStatus(status);
        reservationRepository.save(reservation);

        // Delete the scheduled job for the reservation
        JobKey jobKey = new JobKey(RESERVATION_EXPIRY_JOB_PREFIX + reservation.getId(), RESERVATION_GROUP);
        try {
            scheduler.deleteJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Failed to delete reservation expiry job: ", e);
        }
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

        // Check if the reservation date is before the expiration date
        if (reservation.getReservationDate().isAfter(reservation.getExpirationDate())) {
            throw new RuntimeException("Reservation date cannot be after the expiration date");
        }
    }
}

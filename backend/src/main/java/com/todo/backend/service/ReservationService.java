package com.todo.backend.service;

import com.todo.backend.dao.BookCopyRepository;
import com.todo.backend.dao.BookTitleRepository;
import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.dao.UserRepository;
import com.todo.backend.dto.reservation.CreateReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.dto.reservation.UpdateReservationDto;
import com.todo.backend.dto.reservation.PartialUpdateReservationDto;
import com.todo.backend.entity.BookCopy;
import com.todo.backend.entity.BookTitle;
import com.todo.backend.entity.Reservation;
import com.todo.backend.entity.User;
import com.todo.backend.entity.identity.UserRole;
import com.todo.backend.mapper.ReservationMapper;
import com.todo.backend.scheduler.jobs.ReservationExpiryJob;
import jakarta.transaction.Transactional;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
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
    }    public ResponseReservationDto getReservation(String id, String userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        boolean isOwner = reservation.getUserId().equals(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdminOrLibrarian = user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.LIBRARIAN);
        if (!isOwner && !isAdminOrLibrarian) {
            throw new RuntimeException("You do not have permission to view this reservation");
        }

        return reservationMapper.toResponseDto(reservation);
    }    public List<ResponseReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.toResponseDtoList(reservations);
    }

    public List<ResponseReservationDto> getReservationsByUserId(String userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservationMapper.toResponseDtoList(reservations);
    }

    public ResponseReservationDto createReservation(String userId, CreateReservationDto createReservationDto) {
        LocalDate today = LocalDate.now();

        Reservation reservation = reservationMapper.toEntity(createReservationDto);
        reservation.setUserId(userId);

        validateReservationRules(reservation);

        // Check if a user exists
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if a user has enough balances to reserve
        if (user.getBalance() < createReservationDto.getDeposit()) {
            throw new RuntimeException("User does not have enough balance to reserve");
        }

        // Deduct the deposit from the user's balance
        user.setBalance(user.getBalance() - createReservationDto.getDeposit());
        userRepository.save(user);

        BookCopy availableBookCopy = bookCopyRepository.findFirstByBookTitleIdAndStatus(createReservationDto.getBookTitleId(), "available");
        if (availableBookCopy == null) {
            throw new RuntimeException("No available book copy for reservation");
        }
        reservation.setBookCopyId(availableBookCopy.getId());
        reservation.setStatus("PENDING");
        reservation.setReservationDate(today);

        // Update the status of the book copy to "reserved"
        availableBookCopy.setStatus("RESERVED");
        bookCopyRepository.save(availableBookCopy);

        reservationRepository.save(reservation);

        // Only create the job if the transaction is committed successfully
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                TransactionSynchronization.super.afterCommit();
                createExpiryJob(reservation);
            }
        });

        return reservationMapper.toResponseDto(reservation);
    }

    public ResponseReservationDto updateReservation(String id, String userId, UpdateReservationDto updateReservationDto) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // ADMIN, LIBRARIAN or the user who created the reservation can update it
        boolean isOwner = existingReservation.getUserId().equals(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdminOrLibrarian = user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.LIBRARIAN);
        if (!isOwner && !isAdminOrLibrarian) {
            throw new RuntimeException("You do not have permission to update this reservation");
        }

        // Prevent updating a reservation that is already COMPLETED, CANCELLED or EXPIRED
        if (existingReservation.getStatus().equals("COMPLETED") ||
            existingReservation.getStatus().equals("CANCELLED") ||
            existingReservation.getStatus().equals("EXPIRED")) {
            throw new RuntimeException("Can't update a reservation that is already compeleted, cancelled or expired");
        }

        // Update the user's balance if the deposit changes
        User owner = userRepository.findById(existingReservation.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (existingReservation.getDeposit() > updateReservationDto.getDeposit()) {
            int difference = existingReservation.getDeposit() - updateReservationDto.getDeposit();

            owner.setBalance(owner.getBalance() + difference);
            userRepository.save(owner);
        }
        else if (existingReservation.getDeposit() < updateReservationDto.getDeposit()) {
            int difference = updateReservationDto.getDeposit() - existingReservation.getDeposit();

            if (owner.getBalance() < difference) {
                throw new RuntimeException("User does not have enough balance to update the reservation");
            }

            owner.setBalance(owner.getBalance() - difference);
            userRepository.save(owner);
        }

        LocalDate oldExpirationDate = existingReservation.getExpirationDate();

        // Update the reservation details
        reservationMapper.updateEntityFromDto(updateReservationDto, existingReservation);
        reservationRepository.save(existingReservation);

        // Update scheduled job if the expiration date has changed
        if (!oldExpirationDate.equals(existingReservation.getExpirationDate())) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    TransactionSynchronization.super.afterCommit();
                    deleteScheduledJob(existingReservation);
                    createExpiryJob(existingReservation);
                }
            });
        }

        return reservationMapper.toResponseDto(existingReservation);
    }

    public ResponseReservationDto partialUpdateReservation(String id, String userId, PartialUpdateReservationDto partialUpdateReservationDto) {
        String status = partialUpdateReservationDto.getStatus();

        if (!status.equals("COMPLETED") && !status.equals("CANCELLED")) {
            throw new RuntimeException("Status must be either COMPLETED or CANCELLED");
        }

        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // ADMIN, LIBRARIAN or the user who created the reservation can update it
        boolean isOwner = existingReservation.getUserId().equals(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isAdminOrLibrarian = user.getRole().equals(UserRole.ADMIN) || user.getRole().equals(UserRole.LIBRARIAN);

        if (!isOwner && !isAdminOrLibrarian) {
            throw new RuntimeException("You do not have permission to update this reservation");
        }

        // Prevent updating a reservation that is already COMPLETED, CANCELLED or EXPIRED
        if (existingReservation.getStatus().equals("COMPLETED") ||
            existingReservation.getStatus().equals("CANCELLED") ||
            existingReservation.getStatus().equals("EXPIRED")) {
            throw new RuntimeException("Can't update a reservation that is already completed, cancelled or expired");
        }

        finalizeReservation(existingReservation, status);

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

    private void deleteScheduledJob(Reservation reservation) {
        JobKey jobKey = new JobKey(RESERVATION_EXPIRY_JOB_PREFIX + reservation.getId(), RESERVATION_GROUP);
        try {
            if (!scheduler.checkExists(jobKey)) {
                return;
            }

            scheduler.deleteJob(jobKey);
        }
        catch (SchedulerException e) {
            throw new RuntimeException("Failed to delete reservation expiry job: ", e);
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

        deleteScheduledJob(reservation);
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
}

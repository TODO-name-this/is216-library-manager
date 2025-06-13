package com.todo.backend.controller;

import com.todo.backend.dto.reservation.CreateReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.dto.reservation.UpdateReservationDto;
import com.todo.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        try {
            List<ResponseReservationDto> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching reservations: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservation(@PathVariable String id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            ResponseReservationDto reservation = reservationService.getReservation(id, userId);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching reservation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody CreateReservationDto createReservationDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseReservationDto createdReservation = reservationService.createReservation(userId, createReservationDto);
            return ResponseEntity.ok(createdReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating reservation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable String id, @Valid @RequestBody UpdateReservationDto updateReservationDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseReservationDto updatedReservation = reservationService.updateReservation(id, userId, updateReservationDto);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating reservation: " + e.getMessage());        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(
            @PathVariable String id,
            Authentication authentication) {

        String currentUserId = authentication.getName();

        try {
            reservationService.deleteReservation(id, currentUserId);
            return ResponseEntity.ok("Reservation deleted successfully");
        } catch (AccessDeniedException ade) {
            return ResponseEntity.status(403).body("You do not have permission to delete this reservation");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting reservation: " + e.getMessage());
        }
    }


    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations(Authentication authentication) {
        try {
            String userId = authentication.getName();
            List<ResponseReservationDto> reservations = reservationService.getReservationsByUserId(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching user reservations: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @PostMapping("/{id}/assign-copy")
    public ResponseEntity<?> assignBookCopyToReservation(@PathVariable String id, Authentication authentication) {
        try {
            String userId = authentication.getName();
            ResponseReservationDto updatedReservation = reservationService.assignBookCopyToReservation(id, userId);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error assigning book copy: " + e.getMessage());
        }
    }
}

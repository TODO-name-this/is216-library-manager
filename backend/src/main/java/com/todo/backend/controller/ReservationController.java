package com.todo.backend.controller;

import com.todo.backend.dto.reservation.CreateReservationDto;
import com.todo.backend.dto.reservation.PartialUpdateReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.dto.reservation.UpdateReservationDto;
import com.todo.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
            return ResponseEntity.status(500).body("Error updating reservation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN', 'USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateReservation(@PathVariable String id, @Valid @RequestBody PartialUpdateReservationDto partialUpdateReservationDto, BindingResult result, Authentication authentication) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            String userId = authentication.getName();
            ResponseReservationDto updatedReservation = reservationService.partialUpdateReservation(id, userId, partialUpdateReservationDto);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error partially updating reservation: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Reservation deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting reservation: " + e.getMessage());
        }
    }
}

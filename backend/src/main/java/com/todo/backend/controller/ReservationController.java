package com.todo.backend.controller;

import com.todo.backend.entity.Reservation;
import com.todo.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/secure/add")
    public ResponseEntity<Reservation> addReservation(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody Reservation reservation) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(savedReservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/secure/update/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(@AuthenticationPrincipal Jwt jwt,
                                                         @PathVariable String reservationId,
                                                         @RequestBody Reservation updatedReservation) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Reservation updated = reservationService.updateReservation(reservationId, updatedReservation);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/secure/delete/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable String reservationId) {
        if (!isAdmin(jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            reservationService.deleteReservation(reservationId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isAdmin(Jwt jwt) {
        String role = jwt.getClaimAsString("userType");
        return "admin".equalsIgnoreCase(role);
    }
}

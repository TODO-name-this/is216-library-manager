package com.todo.backend.controller;

import com.todo.backend.dto.reservation.ReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservation(@PathVariable String id) {
        try {
            ResponseReservationDto reservation = reservationService.getReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching reservation: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDto reservationDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseReservationDto createdReservation = reservationService.createReservation(reservationDto);
            return ResponseEntity.ok(createdReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating reservation: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable String id, @Valid @RequestBody ReservationDto reservationDto, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
            }

            ResponseReservationDto updatedReservation = reservationService.updateReservation(id, reservationDto);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating reservation: " + e.getMessage());
        }
    }

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

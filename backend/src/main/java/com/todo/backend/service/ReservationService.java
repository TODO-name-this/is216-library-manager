package com.todo.backend.service;

import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.entity.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservationRepository.existsById(reservation.getId())) {
            throw new RuntimeException("Reservation ID already exists");
        }

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        if (!reservationRepository.existsById(reservation.getId())) {
            throw new RuntimeException("Reservation ID does not exist");
        }

        return reservationRepository.save(reservation);
    }

    public void deleteReservation(String id) {
        reservationRepository.deleteById(id);
    }
}

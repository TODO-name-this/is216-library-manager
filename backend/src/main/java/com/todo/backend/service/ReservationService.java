package com.todo.backend.service;


import com.todo.backend.dao.ReservationRepository;
import com.todo.backend.entity.Reservation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservationRepository.existsById(reservation.getId())) {
            throw new IllegalArgumentException("Reservation with ID already exists: " + reservation.getId());
        }
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(String id, Reservation updated) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));

        if (updated.getReservationDate() != null)
            existing.setReservationDate(updated.getReservationDate());

        if (updated.getExpirationDate() != null)
            existing.setExpirationDate(updated.getExpirationDate());

        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());

        return reservationRepository.save(existing);
    }

    public void deleteReservation(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));
        reservationRepository.delete(reservation);
    }
}

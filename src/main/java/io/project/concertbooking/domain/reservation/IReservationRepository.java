package io.project.concertbooking.domain.reservation;

import io.project.concertbooking.domain.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findByStatusAndRegDtLt(ReservationStatus status, LocalDateTime dateTime);

    Long updateStatusIn(ReservationStatus status, List<Reservation> reservations);

    Optional<Reservation> findById(Long id);
}

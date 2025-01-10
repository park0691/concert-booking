package io.project.concertbooking.domain.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import io.project.concertbooking.domain.seat.enums.SeatStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ISeatRepository {
    Optional<Seat> findById(Long id);

    List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule);

    SeatReservation saveReservation(SeatReservation seatReservation);

    List<SeatReservation> findReservationByStatusAndRegDtLt(SeatReservationStatus status, LocalDateTime dateTime);

    Long updateReservationStatusIn(SeatReservationStatus status, List<SeatReservation> reservations);

    Long updateSeatStatusIn(SeatStatus status, List<Seat> seats);

    Optional<SeatReservation> findReservationById(Long reservationId);
}

package io.project.concertbooking.domain.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;

import java.util.List;
import java.util.Optional;

public interface ISeatRepository {
    Optional<Seat> findById(Long id);

    List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule);

    SeatReservation saveReservation(SeatReservation seatReservation);
}

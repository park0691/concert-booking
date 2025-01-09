package io.project.concertbooking.domain.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;

import java.util.List;

public interface ISeatRepository {
    List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule);
}

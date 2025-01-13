package io.project.concertbooking.domain.concert;

import io.project.concertbooking.domain.concert.enums.SeatStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IConcertRepository {
    Optional<Concert> findById(Long id);

    Page<ConcertSchedule> findAllScheduleByConcert(Concert concert, Pageable pageable);

    Optional<ConcertSchedule> findScheduleById(Long id);

    Optional<Seat> findSeatById(Long id);

    List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule);

    Long updateSeatStatusIn(SeatStatus status, List<Seat> seats);
}

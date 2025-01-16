package io.project.concertbooking.infrastructure.concert.repository;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule);
    List<Seat> findAllByStatus(SeatStatus status);
}

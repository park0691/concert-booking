package io.project.concertbooking.infrastructure.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.ISeatRepository;
import io.project.concertbooking.domain.seat.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements ISeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule) {
        return seatJpaRepository.findAllByConcertSchedule(concertSchedule);
    }
}

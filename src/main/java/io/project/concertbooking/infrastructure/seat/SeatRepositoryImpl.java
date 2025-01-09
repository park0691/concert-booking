package io.project.concertbooking.infrastructure.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.ISeatRepository;
import io.project.concertbooking.domain.seat.Seat;
import io.project.concertbooking.domain.seat.SeatReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements ISeatRepository {

    private final SeatJpaRepository seatJpaRepository;
    private final SeatReservationJpaRepository seatReservationJpaRepository;

    @Override
    public Optional<Seat> findById(Long id) {
        return seatJpaRepository.findById(id);
    }

    @Override
    public List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule) {
        return seatJpaRepository.findAllByConcertSchedule(concertSchedule);
    }

    @Override
    public SeatReservation saveReservation(SeatReservation seatReservation) {
        return seatReservationJpaRepository.save(seatReservation);
    }
}

package io.project.concertbooking.infrastructure.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.ISeatRepository;
import io.project.concertbooking.domain.seat.Seat;
import io.project.concertbooking.domain.seat.SeatReservation;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements ISeatRepository {

    private final SeatJpaRepository seatJpaRepository;
    private final SeatQueryRepository seatQueryRepository;
    private final SeatReservationJpaRepository seatReservationJpaRepository;
    private final SeatReservationQueryRepository seatReservationQueryRepository;

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

    @Override
    public List<SeatReservation> findReservationByStatusAndRegDtLt(SeatReservationStatus status, LocalDateTime dateTime) {
        return seatReservationJpaRepository.findAllByStatusAndRegDtBefore(status, dateTime);
    }

    @Override
    public Long updateReservationStatusIn(SeatReservationStatus status, List<SeatReservation> reservations) {
        return seatReservationQueryRepository.updateReservationStatusIn(status, reservations);
    }

    @Override
    public Long updateSeatStatusIn(SeatStatus status, List<Seat> seats) {
        return seatQueryRepository.updateSeatStatusByIdsIn(status, seats);
    }

    @Override
    public Optional<SeatReservation> findReservationById(Long reservationId) {
        return seatReservationJpaRepository.findById(reservationId);
    }
}

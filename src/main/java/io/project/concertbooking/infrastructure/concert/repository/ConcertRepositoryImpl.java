package io.project.concertbooking.infrastructure.concert.repository;

import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.IConcertRepository;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements IConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ConcertScheduleQueryRepository concertScheduleQueryRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final SeatQueryRepository seatQueryRepository;

    @Override
    public Optional<Concert> findById(Long id) {
        return concertJpaRepository.findById(id);
    }

    @Override
    public Page<ConcertSchedule> findAllScheduleByConcert(Concert concert, Pageable pageable) {
        return concertScheduleQueryRepository.findAllByConcert(concert, pageable);
    }

    @Override
    public Optional<ConcertSchedule> findScheduleById(Long id) {
        return concertScheduleJpaRepository.findById(id);
    }

    @Override
    public Optional<Seat> findSeatById(Long id) {
        return seatJpaRepository.findById(id);
    }

    @Override
    public List<Seat> findAllByConcertSchedule(ConcertSchedule concertSchedule) {
        return seatJpaRepository.findAllByConcertSchedule(concertSchedule);
    }

    @Override
    public Long updateSeatStatusIn(SeatStatus status, List<Seat> seats) {
        return seatQueryRepository.updateSeatStatusByIdsIn(status, seats);
    }
}

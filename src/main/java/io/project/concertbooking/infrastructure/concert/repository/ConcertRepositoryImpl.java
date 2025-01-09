package io.project.concertbooking.infrastructure.concert.repository;

import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.IConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements IConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertScheduleQueryRepository concertScheduleQueryRepository;

    @Override
    public Optional<Concert> findById(Long id) {
        return concertJpaRepository.findById(id);
    }

    @Override
    public Page<ConcertSchedule> findAllScheduleByConcert(Concert concert, Pageable pageable) {
        return concertScheduleQueryRepository.findAllByConcert(concert, pageable);
    }
}

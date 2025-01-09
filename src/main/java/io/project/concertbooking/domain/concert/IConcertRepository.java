package io.project.concertbooking.domain.concert;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IConcertRepository {
    Optional<Concert> findById(Long id);
    Page<ConcertSchedule> findAllScheduleByConcert(Concert concert, Pageable pageable);

}

package io.project.concertbooking.infrastructure.concert.repository;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule, Long> {
}

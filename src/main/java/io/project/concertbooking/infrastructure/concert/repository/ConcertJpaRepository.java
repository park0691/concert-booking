package io.project.concertbooking.infrastructure.concert.repository;

import io.project.concertbooking.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}

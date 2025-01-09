package io.project.concertbooking.infrastructure.seat;

import io.project.concertbooking.domain.seat.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatReservationJpaRepository extends JpaRepository<SeatReservation, Long> {
}

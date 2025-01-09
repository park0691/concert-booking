package io.project.concertbooking.infrastructure.seat;

import io.project.concertbooking.domain.seat.SeatReservation;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatReservationJpaRepository extends JpaRepository<SeatReservation, Long> {
    List<SeatReservation> findAllByStatusAndRegDtBefore(SeatReservationStatus status, LocalDateTime dateTime);
    List<SeatReservation> findAllByStatus(SeatReservationStatus status);
}

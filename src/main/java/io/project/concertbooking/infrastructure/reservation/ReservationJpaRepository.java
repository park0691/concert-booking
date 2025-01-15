package io.project.concertbooking.infrastructure.reservation;

import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByStatusAndRegDtBefore(ReservationStatus status, LocalDateTime dateTime);
}

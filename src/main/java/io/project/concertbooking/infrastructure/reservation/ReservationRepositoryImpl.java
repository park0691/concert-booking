package io.project.concertbooking.infrastructure.reservation;

import io.project.concertbooking.domain.reservation.IReservationRepository;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements IReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> findByStatusAndRegDtLt(ReservationStatus status, LocalDateTime dateTime) {
        return reservationJpaRepository.findAllByStatusAndRegDtBefore(status, dateTime);
    }

    @Override
    public Long updateStatusIn(ReservationStatus status, List<Reservation> reservations) {
        return reservationQueryRepository.updateReservationStatusIn(status, reservations);
    }

    @Override
    public Optional<Reservation> findById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }
}

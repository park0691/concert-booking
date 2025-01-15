package io.project.concertbooking.domain.reservation;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.IConcertRepository;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final IReservationRepository seatRepository;
    private final IConcertRepository concertRepository;

    @Transactional
    public Reservation createReservation(User user, ConcertSchedule concertSchedule, Seat seat) {
        SeatStatus seatStatus = seat.getStatus();
        if (seatStatus.equals(SeatStatus.RESERVED) || seatStatus.equals(SeatStatus.OCCUPIED)) {
            throw new CustomException(ErrorCode.SEAT_NOT_AVAILABLE);
        }

        seat.reserve();

        return seatRepository.save(
                Reservation.createReservation(
                        user, seat, seat.getNumber(), seat.getPrice(), ReservationStatus.RESERVED
                )
        );
    }

    @Transactional
    public void expireReservation(LocalDateTime now) {
        LocalDateTime expiredDt = now.minusMinutes(5L);
        List<Reservation> expireTargets = seatRepository.findByStatusAndRegDtLt(ReservationStatus.RESERVED, expiredDt);
        seatRepository.updateStatusIn(ReservationStatus.EXPIRED, expireTargets);
        List<Seat> seats = expireTargets.stream().map(Reservation::getSeat).toList();
        concertRepository.updateSeatStatusIn(SeatStatus.EMPTY, seats);
    }

    public Reservation findReservation(Long reservationId) {
        return seatRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public void finalizeReservation(Reservation reservation) {
        reservation.confirm();
        seatRepository.save(reservation);
    }
}

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

    private final IReservationRepository reservationRepository;
    private final IConcertRepository concertRepository;
    private final ReservationValidator reservationValidator;

    @Transactional
    public List<Reservation> createReservation(User user, ConcertSchedule concertSchedule, List<Seat> seats) {
        // 예약 완료 상태인지 검증
        reservationValidator.checkIfSeatAvailable(seats);

        // 좌석 예약
        seats.forEach(Seat::reserve);

        // 예약 생성
        return seats.stream()
                .map(seat -> {
                    concertRepository.saveSeat(seat);
                    return reservationRepository.save(
                            Reservation.createReservation(
                                    user, seat, seat.getNumber(), seat.getPrice(), ReservationStatus.RESERVED
                            )
                    );
                })
                .toList();
    }

    @Transactional
    public void expireReservation(LocalDateTime now) {
        LocalDateTime expiredDt = now.minusMinutes(5L);
        List<Reservation> expireTargets = reservationRepository.findByStatusAndRegDtLt(ReservationStatus.RESERVED, expiredDt);
        reservationRepository.updateStatusIn(ReservationStatus.EXPIRED, expireTargets);
        List<Seat> seats = expireTargets.stream().map(Reservation::getSeat).toList();
        concertRepository.updateSeatStatusIn(SeatStatus.EMPTY, seats);
    }

    public Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public void finalizeReservation(Reservation reservation) {
        reservation.confirm();
        reservationRepository.save(reservation);
    }
}

package io.project.concertbooking.domain.seat;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SeatService {

    private final ISeatRepository seatRepository;

    public List<Seat> findAvailableSeats(ConcertSchedule concertSchedule) {
        return seatRepository.findAllByConcertSchedule(concertSchedule).stream()
                .filter(s -> s.getStatus().equals(SeatStatus.EMPTY))
                .toList();
    }

    public Seat findById(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }

    @Transactional
    public SeatReservation createReservation(User user, ConcertSchedule concertSchedule, Seat seat) {
        SeatStatus seatStatus = seat.getStatus();
        if (seatStatus.equals(SeatStatus.RESERVED) || seatStatus.equals(SeatStatus.OCCUPIED)) {
            throw new CustomException(ErrorCode.SEAT_NOT_AVAILABLE);
        }

        seat.reserve();

        return seatRepository.saveReservation(
                SeatReservation.createSeatReservation(
                        user, seat, seat.getNumber(), seat.getPrice(), SeatReservationStatus.RESERVED
                )
        );
    }

    @Transactional
    public void expireReservation() {
        LocalDateTime expiredDt = LocalDateTime.now().minusMinutes(5L);
        List<SeatReservation> expireTargets = seatRepository.findReservationByStatusAndRegDtLt(SeatReservationStatus.RESERVED, expiredDt);
        seatRepository.updateReservationStatusIn(SeatReservationStatus.EXPIRED, expireTargets);
        List<Seat> seats = expireTargets.stream().map(SeatReservation::getSeat).toList();
        seatRepository.updateSeatStatusIn(SeatStatus.EMPTY, seats);
    }
}

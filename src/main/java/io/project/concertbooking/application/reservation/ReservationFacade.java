package io.project.concertbooking.application.reservation;

import io.project.concertbooking.application.reservation.dto.ReservationResult;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.ConcertService;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.ReservationService;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final UserService userService;
    private final ConcertService concertService;

    @Transactional
    public List<ReservationResult> reserve(Long concertScheduleId, Long userId, List<Long> seatIds) {
        User user = userService.findById(userId);
        ConcertSchedule concertSchedule = concertService.findScheduleById(concertScheduleId);
        List<Seat> seats = concertService.findSeatsWithLock(seatIds);
        List<Reservation> reservations = reservationService.createReservation(user, concertSchedule, seats);
        return reservations.stream()
                .map(r -> ReservationResult.of(r, concertSchedule))
                .toList();
    }
}

package io.project.concertbooking.domain.reservation;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationValidator {

    public void checkIfSeatAvailable(List<Seat> seats) {
        seats.stream()
                .filter(seat -> seat.isReserved() || seat.isOccupied())
                .findFirst()
                .ifPresent((seat) -> {
                    throw new CustomException(ErrorCode.SEAT_NOT_AVAILABLE);
                });
    }
}

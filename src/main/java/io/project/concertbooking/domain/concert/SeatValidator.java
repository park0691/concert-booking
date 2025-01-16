package io.project.concertbooking.domain.concert;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatValidator {

    public void checkIfSeatsReservable(List<Seat> seats) {
        seats.stream()
                .filter(seat -> seat.isReserved() || seat.isOccupied())
                .findFirst()
                .ifPresent((seat) -> {
                    throw new CustomException(ErrorCode.SEAT_UNAVAILABLE);
                });
    }
}

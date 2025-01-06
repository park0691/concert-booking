package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetSeatResponse {
    private List<Seat> seats;

    @Builder
    private GetSeatResponse(List<Seat> seats) {
        this.seats = seats;
    }

    public static class Seat {
        private Long seatId;
        private Integer seatNumber;
        private Integer price;

        @Builder
        private Seat(Long seatId, Integer seatNumber, Integer price) {
            this.seatId = seatId;
            this.seatNumber = seatNumber;
            this.price = price;
        }
    }
}
package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReserveSeatResponse {
    private Long seatReservationId;
    private Integer seatNumber;
    private Integer price;

    @Builder
    private ReserveSeatResponse(Long seatReservationId, Integer seatNumber, Integer price) {
        this.seatReservationId = seatReservationId;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}

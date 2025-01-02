package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetReservationSeatResponse {
    private Long seatId;
    private Integer seatNumber;
    private Integer price;

    @Builder
    private GetReservationSeatResponse(Long seatId, Integer seatNumber, Integer price) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}

package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReserveSeatResponse {
    private Long seatReservationId;
    private Long concertScheduleId;
    private Integer seatNumber;
    private Integer price;

    @Builder
    private ReserveSeatResponse(Long seatReservationId, Long concertScheduleId, Integer seatNumber, Integer price) {
        this.seatReservationId = seatReservationId;
        this.concertScheduleId = concertScheduleId;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}

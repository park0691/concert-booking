package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaySeatResponse {
    private Long paymentId;
    private Long seatReservationId;
    private Long concertScheduleId;
    private LocalDateTime concertScheduledDt;

    @Builder
    private PaySeatResponse(Long paymentId, Long seatReservationId, Long concertScheduleId, LocalDateTime concertScheduledDt) {
        this.paymentId = paymentId;
        this.seatReservationId = seatReservationId;
        this.concertScheduleId = concertScheduleId;
        this.concertScheduledDt = concertScheduledDt;
    }
}

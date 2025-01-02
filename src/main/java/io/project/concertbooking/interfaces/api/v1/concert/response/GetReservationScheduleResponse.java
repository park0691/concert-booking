package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetReservationScheduleResponse {
    private Long scheduleId;
    private LocalDateTime scheduleDt;

    @Builder
    private GetReservationScheduleResponse(Long scheduleId, LocalDateTime scheduleDt) {
        this.scheduleId = scheduleId;
        this.scheduleDt = scheduleDt;
    }
}

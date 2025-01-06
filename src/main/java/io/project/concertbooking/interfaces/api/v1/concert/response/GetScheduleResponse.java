package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class GetScheduleResponse {
    private Long concertId;
    private List<Schedule> schedules;

    @Builder
    private GetScheduleResponse(Long concertId, List<Schedule> schedules) {
        this.concertId = concertId;
        this.schedules = schedules;
    }

    @Getter
    public static class Schedule {
        private Long scheduleId;
        private LocalDateTime scheduleDt;

        @Builder
        private Schedule(Long scheduleId, LocalDateTime scheduleDt) {
            this.scheduleId = scheduleId;
            this.scheduleDt = scheduleDt;
        }
    }
}

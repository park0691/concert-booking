package io.project.concertbooking.interfaces.api.v1.concert.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.project.concertbooking.application.concert.dto.ScheduleResult;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleResponse {
    private Long concertId;
    private String concertName;
    private List<Info> schedules;
    private Page page;

    @Getter
    @Setter
    public static class Info {
        private Long scheduleId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduleDt;

        @Builder
        private Info(Long scheduleId, LocalDateTime scheduleDt) {
            this.scheduleId = scheduleId;
            this.scheduleDt = scheduleDt;
        }
    }

    @Getter
    @Setter
    public static class Page {
        private int page;
        private int pageSize;
        private int dataCount;
        private int dataTotalCount;
        private int pageTotalCount;
    }

    @Builder
    private ScheduleResponse(Long concertId, String concertName, List<Info> schedules, Page page) {
        this.concertId = concertId;
        this.concertName = concertName;
        this.schedules = schedules;
        this.page = page;
    }

    public static ScheduleResponse of(ScheduleResult scheduleResult, List<Info> schedules, Page page) {
        return ScheduleResponse.builder()
                .concertId(scheduleResult.getConcertId())
                .concertName(scheduleResult.getConcertName())
                .schedules(schedules)
                .page(page)
                .build();
    }
}

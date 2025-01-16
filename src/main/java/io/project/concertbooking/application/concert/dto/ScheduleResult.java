package io.project.concertbooking.application.concert.dto;

import io.project.concertbooking.domain.concert.Concert;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleResult {
    private Long concertId;
    private String concertName;
    private List<Data> schedules;
    private Page page;

    @Getter
    @Setter
    public static class Data {
        private Long scheduleId;
        private LocalDateTime scheduleDt;

        @Builder
        private Data(Long scheduleId, LocalDateTime scheduleDt) {
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
    private ScheduleResult(Long concertId, String concertName, List<Data> schedules, Page page) {
        this.concertId = concertId;
        this.concertName = concertName;
        this.schedules = schedules;
        this.page = page;
    }

    public static ScheduleResult of(Concert concert, List<Data> schedules, Page page) {
        return ScheduleResult.builder()
                .concertId(concert.getConcertId())
                .concertName(concert.getName())
                .schedules(schedules)
                .page(page)
                .build();
    }
}

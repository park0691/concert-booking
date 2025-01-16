package io.project.concertbooking.domain.concert;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduleValidator {

    public void checkIfReservable(ConcertSchedule schedule, LocalDateTime now) {
        LocalDateTime midNightTimeOfScheduleDt = schedule.getScheduleDt().toLocalDate().atStartOfDay();
        if (now.isAfter(midNightTimeOfScheduleDt)) {
            throw new CustomException(ErrorCode.SCHEDULE_UNAVAILABLE);
        }
    }
}

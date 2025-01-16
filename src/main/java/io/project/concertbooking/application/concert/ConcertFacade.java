package io.project.concertbooking.application.concert;

import io.project.concertbooking.application.concert.dto.ScheduleResult;
import io.project.concertbooking.application.concert.dto.mapper.ConcertResultMapper;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final ConcertResultMapper resultMapper;

    public ScheduleResult getSchedules(Long concertId, Pageable pageable) {
        Concert concert = concertService.findById(concertId);
        Page<ConcertSchedule> schedules = concertService.findSchedulesBy(concert, pageable);
        return ScheduleResult.of(concert,
                schedules.getContent().stream()
                        .map(resultMapper::toScheduleDataOfResult)
                        .toList(),
                resultMapper.toSchedulePageOfResult(schedules)
        );
    }
}

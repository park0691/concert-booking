package io.project.concertbooking.domain.concert;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ConcertService {

    private final IConcertRepository concertRepository;

    public Concert findById(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
    }

    public Page<ConcertSchedule> findScheduleByConcert(Concert concert, Pageable pageable) {
        return concertRepository.findAllScheduleByConcert(concert, pageable);
    }

    public ConcertSchedule findScheduleById(Long id) {
        return concertRepository.findScheduleById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
    }

    public List<Seat> findAvailableSeats(ConcertSchedule concertSchedule) {
        return concertRepository.findAllByConcertSchedule(concertSchedule).stream()
                .filter(s -> s.getStatus().equals(SeatStatus.EMPTY))
                .toList();
    }

    public Seat findSeatById(Long seatId) {
        return concertRepository.findSeatById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));
    }
}

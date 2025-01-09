package io.project.concertbooking.domain.seat;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final ISeatRepository seatRepository;

    public List<Seat> findAvailableSeats(ConcertSchedule concertSchedule) {
        return seatRepository.findAllByConcertSchedule(concertSchedule).stream()
                .filter(s -> s.getStatus().equals(SeatStatus.EMPTY))
                .toList();
    }
}

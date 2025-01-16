package io.project.concertbooking.interfaces.scheduler;

import io.project.concertbooking.domain.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60000)
    public void expireReservation() {
        reservationService.expireReservation(LocalDateTime.now());
    }
}

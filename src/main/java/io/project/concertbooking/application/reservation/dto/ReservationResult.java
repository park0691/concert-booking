package io.project.concertbooking.application.reservation.dto;

import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResult {
    private Long concertScheduleId;
    private LocalDateTime concertScheduleDt;
    private Long reservationId;
    private Integer seatNumber;
    private Integer price;
    private LocalDateTime expDt;

    @Builder
    private ReservationResult(Long concertScheduleId, LocalDateTime concertScheduleDt, Long reservationId, Integer seatNumber, Integer price, LocalDateTime expDt) {
        this.concertScheduleId = concertScheduleId;
        this.concertScheduleDt = concertScheduleDt;
        this.reservationId = reservationId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.expDt = expDt;
    }

    public static ReservationResult of(Reservation reservation, ConcertSchedule schedule) {
        return ReservationResult.builder()
                .concertScheduleId(schedule.getConcertScheduleId())
                .concertScheduleDt(schedule.getScheduleDt())
                .reservationId(reservation.getReservationId())
                .seatNumber(reservation.getSeatNumber())
                .price(reservation.getPrice())
                .expDt(reservation.getRegDt().plusMinutes(5L))
                .build();
    }
}

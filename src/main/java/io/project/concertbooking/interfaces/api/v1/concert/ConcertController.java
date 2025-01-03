package io.project.concertbooking.interfaces.api.v1.concert;

import io.project.concertbooking.interfaces.api.v1.concert.request.PaySeatRequest;
import io.project.concertbooking.interfaces.api.v1.concert.request.ReserveSeatRequest;
import io.project.concertbooking.interfaces.api.v1.concert.response.*;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

    @PostMapping("/{concertScheduleId}/queue-token")
    public ApiResponse<?> createQueueToken(@PathVariable("concertScheduleId") String concertScheduleId) {
        return ApiResponse.ok(CreateQueueTokenResponse.builder()
                .token("123e4567-e89b-12d3-a456-426614174000")
                .build());
    }

    @GetMapping("/{concertId}/reservation/schedules")
    public ApiResponse<?> getReservationSchedule(
            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertId") Long concertId
    ) {
        return ApiResponse.ok(List.of(
                        GetReservationScheduleResponse.builder()
                                .scheduleId(11L)
                                .scheduleDt(LocalDateTime.of(2025, Month.MAY, 1, 15, 0))
                                .build(),
                        GetReservationScheduleResponse.builder()
                                .scheduleId(12L)
                                .scheduleDt(LocalDateTime.of(2025, Month.MAY, 8, 15, 0))
                                .build()
                )
        );
    }

    @GetMapping("/{concertScheduleId}/reservation/seats")
    public ApiResponse<?> getReservationSeat(
            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertScheduleId") Long concertScheduleId
    ) {
        return ApiResponse.ok(List.of(
                        GetReservationSeatResponse.builder()
                                .seatId(1L)
                                .seatNumber(10)
                                .price(10000)
                                .build(),
                        GetReservationSeatResponse.builder()
                                .seatId(2L)
                                .seatNumber(12)
                                .price(20000)
                                .build()
                )
        );
    }

    @PostMapping("/{concertScheduleId}/reservation/seats/{seatId}")
    public ApiResponse<?> reserveSeat(
            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertScheduleId") Long concertScheduleId,
            @PathVariable("seatId") Long seatId,
            @RequestBody ReserveSeatRequest reserveSeatRequest
    ) {
        return ApiResponse.ok(ReserveSeatResponse.builder()
                .seatReservationId(1L)
                .seatNumber(11)
                .price(10000)
                .build());
    }

    @PostMapping("/{concertScheduleId}/reservation/seats/payments/{seatId}")
    public ApiResponse<?> paySeat(
            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertScheduleId") Long concertScheduleId,
            @PathVariable("seatId") Long seatId,
            @RequestBody PaySeatRequest paySeatRequest
    ) {
        return ApiResponse.ok(PaySeatResponse.builder()
                .paymentId(12L)
                .seatReservationId(11L)
                .concertScheduleId(1L)
                .concertScheduledDt(LocalDateTime.of(2025, Month.MAY, 8, 15, 0))
                .build());
    }
}
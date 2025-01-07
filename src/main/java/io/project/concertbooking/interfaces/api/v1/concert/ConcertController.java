package io.project.concertbooking.interfaces.api.v1.concert;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.concert.request.ReserveSeatRequest;
import io.project.concertbooking.interfaces.api.v1.concert.response.GetScheduleResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.GetSeatResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.ReserveSeatResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

    @GetMapping("/{concertId}/schedules")
    public ApiResponse<?> getSchedule(
//            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertId") Long concertId
    ) {
        return ApiResponse.ok(GetScheduleResponse.builder()
                        .concertId(2L)
                        .schedules(List.of(
                                        GetScheduleResponse.Schedule.builder()
                                                .scheduleId(11L)
                                                .scheduleDt(LocalDateTime.of(2025, Month.MAY, 1, 15, 0))
                                                .build(),
                                        GetScheduleResponse.Schedule.builder()
                                                .scheduleId(12L)
                                                .scheduleDt(LocalDateTime.of(2025, Month.MAY, 8, 15, 0))
                                                .build()
                                )
                        )
                        .build()
        );
    }

    @GetMapping("/schedules/{concertScheduleId}/seats")
    public ApiResponse<?> getSeat(
//            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertScheduleId") Long concertScheduleId
    ) {
        return ApiResponse.ok(GetSeatResponse.builder()
                        .concertScheduleId(11L)
                        .seats(List.of(
                                GetSeatResponse.Seat.builder()
                                        .seatId(1L)
                                        .seatNumber(10)
                                        .price(10000)
                                        .build(),
                                GetSeatResponse.Seat.builder()
                                        .seatId(2L)
                                        .seatNumber(12)
                                        .price(20000)
                                        .build()
                                )
                        )
                        .build()
        );
    }

    @PostMapping("/schedules/{concertScheduleId}/reservation/seats/{seatId}")
    public ApiResponse<?> reserveSeat(
//            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("concertScheduleId") Long concertScheduleId,
            @PathVariable("seatId") Long seatId,
            @RequestBody ReserveSeatRequest reserveSeatRequest
    ) {
        return ApiResponse.ok(ReserveSeatResponse.builder()
                .seatReservationId(1L)
                .concertScheduleId(22L)
                .seatNumber(11)
                .price(10000)
                .build());
    }
}
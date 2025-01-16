package io.project.concertbooking.interfaces.api.v1.concert;

import io.project.concertbooking.application.reservation.ReservationFacade;
import io.project.concertbooking.common.annotation.TokenRequired;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.concert.request.ReservationRequest;
import io.project.concertbooking.interfaces.api.v1.concert.response.GetScheduleResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.GetSeatResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.ReservationResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.mapper.ConcertResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Tag(name = "콘서트 조회 및 예약")
@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ReservationFacade reservationFacade;
    private final ConcertResponseMapper responseMapper;

    @Operation(summary = "콘서트 스케줄 조회", description = "콘서트 ID로 콘서트의 스케줄을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @TokenRequired
    @GetMapping("/{concertId}/schedules")
    public ApiResponse<?> getSchedule(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("concertId") @Parameter(description = "콘서트 ID", example = "9") Long concertId
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

    @Operation(summary = "콘서트 좌석 조회", description = "콘서트 스케줄 ID로 예약 가능한 콘서트 좌석을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @TokenRequired
    @GetMapping("/schedules/{concertScheduleId}/seats")
    public ApiResponse<?> getSeat(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("concertScheduleId") @Parameter(description = "콘서트 스케줄 ID", example = "1") Long concertScheduleId
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

    @Operation(summary = "콘서트 좌석 예약(임시 배정)", description = "사용자, 콘서트 스케줄 ID로 콘서트 좌석을 예약(임시 배정)합니다. 예약 후 5분 이내 결제하지 않은 경우 좌석의 임시 배정은 취소됩니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 성공")
    })
    @TokenRequired
    @PostMapping("/schedules/{concertScheduleId}/reservations")
    public ApiResponse<?> reservations(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("concertScheduleId") @Parameter(description = "콘서트 스케줄 ID", example = "1") Long concertScheduleId,
            @RequestBody ReservationRequest reservationRequest
    ) {
        return ApiResponse.ok(ReservationResponse.of(
                reservationFacade.reserve(concertScheduleId, reservationRequest.getUserId(), reservationRequest.getSeatIds())
                        .stream()
                        .map(responseMapper::toReservationInfoOfResponse)
                        .toList()
        ));
    }
}
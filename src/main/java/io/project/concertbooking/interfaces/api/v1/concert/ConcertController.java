package io.project.concertbooking.interfaces.api.v1.concert;

import io.project.concertbooking.application.concert.ConcertFacade;
import io.project.concertbooking.application.concert.dto.ScheduleResult;
import io.project.concertbooking.application.reservation.ReservationFacade;
import io.project.concertbooking.common.annotation.TokenRequired;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.concert.request.ReservationRequest;
import io.project.concertbooking.interfaces.api.v1.concert.response.SeatResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.ReservationResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.ScheduleResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.mapper.ConcertResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "콘서트 조회 및 예약")
@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;
    private final ReservationFacade reservationFacade;
    private final ConcertResponseMapper responseMapper;

    @Operation(summary = "콘서트 스케줄 조회", description = "콘서트 ID로 콘서트의 스케줄을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @TokenRequired
    @GetMapping("/{concertId}/schedules")
    public ApiResponse<?> schedules(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("concertId") @Parameter(description = "콘서트 ID", example = "9") Long concertId,
            @RequestParam(defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") int page,
            @RequestParam(defaultValue = "5") @Parameter(description = "페이지 사이즈", example = "5") int size
    ) {
        ScheduleResult scheduleResult = concertFacade.getSchedules(concertId, PageRequest.of(page - 1, size));
        return ApiResponse.ok(ScheduleResponse.of(
                scheduleResult,
                scheduleResult.getSchedules().stream()
                        .map(responseMapper::toScheduleInfoOfResponse)
                        .toList(),
                responseMapper.toSchedulePageOfResponse(scheduleResult.getPage())
        ));
    }

    @Operation(summary = "콘서트 좌석 조회", description = "콘서트 스케줄 ID로 예약 가능한 콘서트 좌석을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @TokenRequired
    @GetMapping("/schedules/{concertScheduleId}/seats")
    public ApiResponse<?> seats(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("concertScheduleId") @Parameter(description = "콘서트 스케줄 ID", example = "1") Long concertScheduleId
    ) {
        return ApiResponse.ok(SeatResponse.of(
                concertFacade.getSeats(concertScheduleId).stream()
                        .map(responseMapper::toSeatInfoOfResponse)
                        .toList()
        ));
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
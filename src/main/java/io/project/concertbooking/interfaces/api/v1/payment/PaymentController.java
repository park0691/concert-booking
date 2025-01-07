package io.project.concertbooking.interfaces.api.v1.payment;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.payment.request.PaySeatRequest;
import io.project.concertbooking.interfaces.api.v1.payment.response.PaySeatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;

@Tag(name = "결제")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Operation(summary = "결제", description = "결제 처리 후, 좌석의 소유권을 유저에게 배정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 성공")
    })
    @PostMapping("/seat-reservations/{seatReservationId}")
    public ApiResponse<?> paySeat(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("seatReservationId") @Parameter(description = "좌석 예약 ID", example = "1") Long seatReservationId,
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

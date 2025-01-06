package io.project.concertbooking.interfaces.api.v1.payment;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.payment.request.PaySeatRequest;
import io.project.concertbooking.interfaces.api.v1.payment.response.PaySeatResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.Month;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @PostMapping("/seat-reservations/{seatReservationId}")
    public ApiResponse<?> paySeat(
            @RequestHeader("Queue-Token") String queueToken,
            @PathVariable("seatReservationId") Long seatReservationId,
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

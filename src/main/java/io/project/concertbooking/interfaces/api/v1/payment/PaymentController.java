package io.project.concertbooking.interfaces.api.v1.payment;

import io.project.concertbooking.application.payment.PaymentFacade;
import io.project.concertbooking.common.annotation.TokenRequired;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.payment.request.PaymentRequest;
import io.project.concertbooking.interfaces.api.v1.payment.response.mapper.PaymentResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "결제")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;
    private final PaymentResponseMapper responseMapper;

    @Operation(summary = "결제", description = "결제 처리 후, 좌석의 소유권을 유저에게 배정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 성공")
    })
    @TokenRequired
    @PostMapping("/reservations/{reservationId}")
    public ApiResponse<?> payments(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken,
            @PathVariable("reservationId") @Parameter(description = "예약 ID", example = "1") Long reservationId,
            @RequestBody PaymentRequest request
    ) {
        return ApiResponse.ok(responseMapper.toPaymentResponse(
                        paymentFacade.processPayments(queueToken, request.getUserId(), reservationId, request.getPaymentPrice())
                )
        );
    }
}

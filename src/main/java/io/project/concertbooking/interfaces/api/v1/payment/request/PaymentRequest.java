package io.project.concertbooking.interfaces.api.v1.payment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "PaymentRequest", description = "좌석 결제 요청")
@Getter
public class PaymentRequest {
    @Schema(description = "사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "결제 금액", example = "31000")
    private Integer paymentPrice;

    @Builder
    private PaymentRequest(Long userId, Integer paymentPrice) {
        this.userId = userId;
        this.paymentPrice = paymentPrice;
    }
}

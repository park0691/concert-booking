package io.project.concertbooking.application.payment.dto;

import io.project.concertbooking.domain.payment.Payment;
import io.project.concertbooking.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResult {
    private Long paymentId;
    private Long reservationId;
    private Integer price;
    private LocalDateTime paymentDt;

    @Builder
    private PaymentResult(Long paymentId, Long reservationId, Integer price, LocalDateTime paymentDt) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.price = price;
        this.paymentDt = paymentDt;
    }

    public static PaymentResult of(Payment payment, Reservation reservation) {
        return PaymentResult.builder()
                .paymentId(payment.getPaymentId())
                .reservationId(reservation.getReservationId())
                .price(payment.getPrice())
                .paymentDt(payment.getRegDt())
                .build();
    }
}

package io.project.concertbooking.interfaces.api.v1.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {
    private Long paymentId;
    private Long reservationId;
    private Integer price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDt;
}

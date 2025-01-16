package io.project.concertbooking.interfaces.api.v1.payment.response.mapper;

import io.project.concertbooking.application.payment.dto.PaymentResult;
import io.project.concertbooking.interfaces.api.v1.payment.response.PaymentResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentResponseMapper {

    PaymentResponse toPaymentResponse(PaymentResult paymentResult);
}

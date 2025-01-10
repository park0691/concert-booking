package io.project.concertbooking.domain.payment.enums.converter;

import io.project.concertbooking.domain.payment.enums.PaymentStatus;
import jakarta.persistence.AttributeConverter;

public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus attribute) {
        return attribute.getDbCode();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String s) {
        return PaymentStatus.fromDbCode(s);
    }
}

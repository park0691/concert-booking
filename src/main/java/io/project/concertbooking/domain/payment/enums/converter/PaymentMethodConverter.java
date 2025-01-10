package io.project.concertbooking.domain.payment.enums.converter;

import io.project.concertbooking.domain.payment.enums.PaymentMethod;
import jakarta.persistence.AttributeConverter;

public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute.getDbCode();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String s) {
        return PaymentMethod.fromDbCode(s);
    }
}

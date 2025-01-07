package io.project.concertbooking.domain.point.enums.converter;

import io.project.concertbooking.domain.point.enums.TransactionType;
import jakarta.persistence.AttributeConverter;

public class TransactionTypeConverter implements AttributeConverter<TransactionType, String> {

    @Override
    public String convertToDatabaseColumn(TransactionType attribute) {
        return attribute.getDbCode();
    }

    @Override
    public TransactionType convertToEntityAttribute(String s) {
        return TransactionType.fromDbCode(s);
    }
}

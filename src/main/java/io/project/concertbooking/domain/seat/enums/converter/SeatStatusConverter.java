package io.project.concertbooking.domain.seat.enums.converter;

import io.project.concertbooking.domain.seat.enums.SeatStatus;
import jakarta.persistence.AttributeConverter;

public class SeatStatusConverter implements AttributeConverter<SeatStatus, String> {

    @Override
    public String convertToDatabaseColumn(SeatStatus attribute) {
        return attribute.getDbCode();
    }

    @Override
    public SeatStatus convertToEntityAttribute(String s) {
        return SeatStatus.fromDbCode(s);
    }
}
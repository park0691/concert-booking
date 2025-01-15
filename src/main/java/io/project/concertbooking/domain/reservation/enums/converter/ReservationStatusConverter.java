package io.project.concertbooking.domain.reservation.enums.converter;

import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import jakarta.persistence.AttributeConverter;

public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {

    @Override
    public String convertToDatabaseColumn(ReservationStatus attribute) {
        return attribute.getDbCode();
    }

    @Override
    public ReservationStatus convertToEntityAttribute(String s) {
        return ReservationStatus.fromDbCode(s);
    }
}

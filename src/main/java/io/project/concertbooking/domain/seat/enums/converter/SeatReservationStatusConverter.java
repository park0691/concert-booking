package io.project.concertbooking.domain.seat.enums.converter;

import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import jakarta.persistence.AttributeConverter;

public class SeatReservationStatusConverter implements AttributeConverter<SeatReservationStatus, String> {

    @Override
    public String convertToDatabaseColumn(SeatReservationStatus attribute) {
        return attribute.getDbCode();
    }

    @Override
    public SeatReservationStatus convertToEntityAttribute(String s) {
        return SeatReservationStatus.fromDbCode(s);
    }
}

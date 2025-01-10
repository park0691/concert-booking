package io.project.concertbooking.domain.queue.enums.converter;

import io.project.concertbooking.domain.queue.enums.QueueStatus;
import jakarta.persistence.AttributeConverter;

public class QueueStatusConverter implements AttributeConverter<QueueStatus, String> {

    @Override
    public String convertToDatabaseColumn(QueueStatus attribute) {
        return attribute.getDbCode();
    }

    @Override
    public QueueStatus convertToEntityAttribute(String s) {
        return QueueStatus.fromDbCode(s);
    }
}

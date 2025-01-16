package io.project.concertbooking.interfaces.api.v1.queue.response.mapper;

import io.project.concertbooking.application.queue.dto.QueueResult;
import io.project.concertbooking.application.queue.dto.QueueStatusResult;
import io.project.concertbooking.interfaces.api.v1.queue.response.QueueResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QueueResponseMapper {

    QueueResponse toQueueResponse(QueueResult queueResult);

    QueueResponse.Status toQueueStatusResponse(QueueStatusResult queueStatusResult);
}

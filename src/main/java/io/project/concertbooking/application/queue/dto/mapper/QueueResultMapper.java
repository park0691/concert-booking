package io.project.concertbooking.application.queue.dto.mapper;

import io.project.concertbooking.application.queue.dto.QueueResult;
import io.project.concertbooking.domain.queue.Queue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QueueResultMapper {

    @Mapping(target = "expireDt", source = "expDt")
    QueueResult toQueueResult(Queue queue);
}

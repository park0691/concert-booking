package io.project.concertbooking.domain.queue;

import io.project.concertbooking.domain.queue.enums.QueueStatus;

import java.util.Optional;

public interface IQueueRepository {
    Queue save(Queue queue);

    Optional<Queue> findByIdAndStatus(Long id, QueueStatus status);

    Optional<Queue> findByToken(String token);

    Integer findCountByIdAndStatus(Long id, QueueStatus status);
}

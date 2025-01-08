package io.project.concertbooking.domain.queue;

import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;

import java.util.Optional;

public interface IQueueRepository {
    Queue save(Queue queue);

    Optional<Queue> findByUserAndStatus(User user, QueueStatus status);

    Optional<Queue> findByToken(String token);

    Integer findCountByIdAndStatus(Long id, QueueStatus status);
}

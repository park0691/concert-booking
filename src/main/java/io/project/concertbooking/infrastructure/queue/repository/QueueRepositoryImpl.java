package io.project.concertbooking.infrastructure.queue.repository;

import io.project.concertbooking.domain.queue.IQueueRepository;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements IQueueRepository {

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Queue save(Queue queue) {
        return queueJpaRepository.save(queue);
    }

    @Override
    public Optional<Queue> findByIdAndStatus(Long id, QueueStatus status) {
        return queueJpaRepository.findByIdAndStatus(id, status);
    }
}

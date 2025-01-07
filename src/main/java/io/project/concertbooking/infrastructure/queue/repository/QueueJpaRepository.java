package io.project.concertbooking.infrastructure.queue.repository;

import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByIdAndStatus(Long id, QueueStatus status);
}

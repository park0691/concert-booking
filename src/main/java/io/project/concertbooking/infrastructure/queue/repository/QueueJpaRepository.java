package io.project.concertbooking.infrastructure.queue.repository;

import io.project.concertbooking.domain.queue.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
}

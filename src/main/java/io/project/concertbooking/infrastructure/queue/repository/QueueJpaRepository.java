package io.project.concertbooking.infrastructure.queue.repository;

import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByIdAndStatus(Long id, QueueStatus status);

    Optional<Queue> findByToken(String token);

    @Query("SELECT count(q.queueId) FROM Queue q WHERE q.queueId < :queueId AND q.status = :status")
    Integer findCountByIdAndStatus(@Param("id") Long id, @Param("status") QueueStatus status);
}

package io.project.concertbooking.infrastructure.queue.repository;

import io.project.concertbooking.domain.queue.IQueueRepository;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements IQueueRepository {

    private final QueueJpaRepository queueJpaRepository;
    private final QueueQueryRepository queueQueryRepository;

    @Override
    public Queue save(Queue queue) {
        return queueJpaRepository.save(queue);
    }

    @Override
    public Optional<Queue> findByUserAndStatus(User user, QueueStatus status) {
        return queueJpaRepository.findByUserAndStatus(user, status);
    }

    @Override
    public Optional<Queue> findByToken(String token) {
        return queueJpaRepository.findByToken(token);
    }

    @Override
    public Integer findCountByIdAndStatus(Long id, QueueStatus status) {
        return queueJpaRepository.findCountByIdAndStatus(id, status);
    }

    @Override
    public Long updateStatusByExpDtLt(QueueStatus status, LocalDateTime dateTime) {
        return queueQueryRepository.updateStatusByExpDtLt(status, dateTime);
    }

    @Override
    public Long findCountByStatus(QueueStatus status) {
        return queueQueryRepository.findCountByStatus(status);
    }

    @Override
    public List<Queue> findAllWaitingLimit(Integer limit) {
        return queueQueryRepository.findAllByStatusOrderByIdAscLimit(QueueStatus.WAITING, limit);
    }

    @Override
    public Long updateStatusByIds(QueueStatus status, List<Long> ids) {
        return queueQueryRepository.updateStatusByIds(status, ids);
    }
}

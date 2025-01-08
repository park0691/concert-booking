package io.project.concertbooking.domain.queue;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final IQueueRepository queueRepository;

    public Optional<Queue> findByUserAndStatus(User user, QueueStatus queueStatus) {
        return queueRepository.findByUserAndStatus(user, queueStatus);
    }

    public void expire(Queue queue) {
        queue.expireQueueToken();
        queueRepository.save(queue);
    }

    public String createQueueToken(User user) {
        String token = UUID.randomUUID().toString();
        Queue queue = queueRepository.save(
                Queue.create(user, token)
        );

        return queue.getToken();
    }

    public Queue findByToken(String token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public Integer findWaitingCount(Long queueId) {
        return queueRepository.findCountByIdAndStatus(queueId, QueueStatus.WAITING);
    }
}

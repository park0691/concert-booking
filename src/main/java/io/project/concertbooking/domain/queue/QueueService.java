package io.project.concertbooking.domain.queue;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.common.util.TokenGenerateUtil;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class QueueService {

    private final IQueueRepository queueRepository;

    @Value("${queue.max-size}")
    private int QUEUE_MAX_SIZE;

    @Value("${queue.expire-time-min}")
    private int EXPIRE_TIME_MIN;

    public Optional<Queue> findByUserAndStatus(User user, QueueStatus queueStatus) {
        return queueRepository.findByUserAndStatus(user, queueStatus);
    }

    @Transactional
    public void expire(Queue queue) {
        queue.expireQueueToken();
        queueRepository.save(queue);
    }

    @Transactional
    public String createQueueToken(User user, LocalDateTime now) {
        String token = TokenGenerateUtil.generateUUIDToken();
        Queue queue = queueRepository.save(
                Queue.create(user, token, now.plusMinutes(EXPIRE_TIME_MIN))
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

    public void validateToken(String token) {
        Queue queue = queueRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));

        if (queue.isExpired() || queue.isWaiting()) {
            throw new CustomException(ErrorCode.TOKEN_NOT_ACTIVE);
        }
    }

    @Transactional
    public void dequeueByExpiringOutdatedToken() {
        queueRepository.updateStatusByExpDtLt(QueueStatus.EXPIRED, LocalDateTime.now());
    }

    @Transactional
    public void enqueueByActivatingWaitingToken() {
        int activeQueueCount = queueRepository.findCountByStatus(QueueStatus.ACTIVATED).intValue();
        int activeCandidateQueueCount = QUEUE_MAX_SIZE - activeQueueCount;

        if (activeCandidateQueueCount > 0) {
            List<Long> activeCandidateQueueIds = queueRepository.findAllWaitingLimit(activeCandidateQueueCount)
                    .stream()
                    .map(Queue::getQueueId)
                    .toList();
            queueRepository.updateStatusByIds(QueueStatus.ACTIVATED, activeCandidateQueueIds);
        }
    }
}

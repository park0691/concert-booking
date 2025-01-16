package io.project.concertbooking.domain.queue;

import io.project.concertbooking.common.constants.QueueConstants;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.common.util.TokenGenerateUtil;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
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
    private final QueueConstants queueConstants;

    public Optional<Queue> findBy(User user, QueueStatus queueStatus) {
        return queueRepository.findByUserAndStatus(user, queueStatus);
    }

    @Transactional
    public void expire(Queue queue) {
        queue.expireQueueToken();
        queueRepository.save(queue);
    }

    @Transactional
    public Queue createQueueToken(User user, LocalDateTime now) {
        String token = TokenGenerateUtil.generateUUIDToken();
        return queueRepository.save(
                Queue.create(user, token, now.plusMinutes(queueConstants.getExpireTimeMin()))
        );
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
        int activeCandidateQueueCount = queueConstants.getMaxSize() - activeQueueCount;

        if (activeCandidateQueueCount > 0) {
            List<Long> activeCandidateQueueIds = queueRepository.findAllWaitingLimit(activeCandidateQueueCount)
                    .stream()
                    .map(Queue::getQueueId)
                    .toList();
            queueRepository.updateStatusByIds(QueueStatus.ACTIVATED, activeCandidateQueueIds);
        }
    }
}

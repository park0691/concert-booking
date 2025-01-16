package io.project.concertbooking.application.queue;

import io.project.concertbooking.application.queue.dto.QueueStatusResult;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.QueueService;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class QueueFacade {

    private final UserService userService;
    private final QueueService queueService;

    @Transactional
    public String issueQueueToken(Long userId) {
        User user = userService.findById(userId);

        queueService.findBy(user, QueueStatus.WAITING)
                .ifPresent(queueService::expire);

        return queueService.createQueueToken(user, LocalDateTime.now());
    }

    public QueueStatusResult findQueueStatus(String token) {
        Queue queue = queueService.findByToken(token);

        return QueueStatusResult.of(
                queue, queue.getStatus().equals(QueueStatus.WAITING) ?
                        queueService.findWaitingCount(queue.getQueueId()) : 0
        );
    }
}

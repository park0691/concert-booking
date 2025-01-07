package io.project.concertbooking.domain.queue;

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

    public Optional<Queue> findByIdAndStatus(Long userId, QueueStatus queueStatus) {
        return queueRepository.findByIdAndStatus(userId, queueStatus);
    }

    public void expire(Queue queue) {
        queue.expireQueueToken();
        queueRepository.save(queue);
    }

    public Queue createQueueToken(User user) {
        String token = UUID.randomUUID().toString();
        return queueRepository.save(
                Queue.create(user, token)
        );
    }
}

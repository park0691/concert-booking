package io.project.concertbooking.interfaces.scheduler;

import io.project.concertbooking.domain.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueService queueService;

    @Scheduled(fixedDelay = 60000)
    public void dequeue() {
        queueService.dequeueByExpiringOutdatedToken();
    }

    @Scheduled(fixedDelay = 60000)
    public void enqueue() {
        queueService.enqueueByActivatingWaitingToken();
    }
}

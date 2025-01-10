package io.project.concertbooking.application.queue.dto;

import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class QueueStatusResult {
    private String token;
    private String status;
    private Integer waitingCount;
    private LocalDateTime createDt;
    private LocalDateTime expireDt;

    @Builder
    private QueueStatusResult(String token, String status, Integer waitingCount, LocalDateTime createDt, LocalDateTime expireDt) {
        this.token = token;
        this.status = status;
        this.waitingCount = waitingCount;
        this.createDt = createDt;
        this.expireDt = expireDt;
    }

    public static QueueStatusResult of(Queue queue, Integer waitingCount) {
        return QueueStatusResult.builder()
                .token(queue.getToken())
                .status(queue.getStatus().getDbCode())
                .waitingCount(waitingCount)
                .createDt(queue.getRegDt())
                .expireDt(queue.getStatus().equals(QueueStatus.ACTIVATED) ? queue.getExpDt() : null)
                .build();
    }
}

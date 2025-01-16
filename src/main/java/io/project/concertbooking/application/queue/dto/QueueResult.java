package io.project.concertbooking.application.queue.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueueResult {
    private String token;
    private LocalDateTime expireDt;
}

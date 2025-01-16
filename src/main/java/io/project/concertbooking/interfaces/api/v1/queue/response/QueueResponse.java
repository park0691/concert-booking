package io.project.concertbooking.interfaces.api.v1.queue.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QueueResponse {
    private String token;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDt;

    @Getter
    @Setter
    public static class Status extends QueueResponse {
        private String status;
        private Integer waitingCount;
        private LocalDateTime createDt;
    }
}

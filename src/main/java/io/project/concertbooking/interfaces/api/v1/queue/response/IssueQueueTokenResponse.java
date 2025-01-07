package io.project.concertbooking.interfaces.api.v1.queue.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IssueQueueTokenResponse {
    private String token;

    @Builder
    private IssueQueueTokenResponse(String token) {
        this.token = token;
    }
}

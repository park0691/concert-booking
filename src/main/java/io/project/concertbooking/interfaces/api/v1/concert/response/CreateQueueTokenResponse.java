package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateQueueTokenResponse {
    private String token;

    @Builder
    private CreateQueueTokenResponse(String token) {
        this.token = token;
    }
}

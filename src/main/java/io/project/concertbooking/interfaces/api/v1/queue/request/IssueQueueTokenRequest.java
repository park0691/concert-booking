package io.project.concertbooking.interfaces.api.v1.queue.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "IssueQueueTokenRequest", description = "대기열 토큰 발급 요청")
@Getter
public class IssueQueueTokenRequest {
    @Schema(description = "사용자 ID", example = "2")
    private Long userId;
}

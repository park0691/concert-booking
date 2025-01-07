package io.project.concertbooking.interfaces.api.v1.queue;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.queue.request.IssueQueueTokenRequest;
import io.project.concertbooking.interfaces.api.v1.queue.response.IssueQueueTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대기열")
@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {

    @Operation(summary = "대기열 토큰 발급", description = "콘서트 예약을 위한 대기열 토큰을 발급합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @PostMapping("/tokens")
    public ApiResponse<?> issueQueueToken(@RequestBody IssueQueueTokenRequest request) {
        return ApiResponse.ok(IssueQueueTokenResponse.builder()
                .token("123e4567-e89b-12d3-a456-426614174000")
                .build());
    }

}

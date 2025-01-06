package io.project.concertbooking.interfaces.api.v1.queue;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.queue.request.IssueQueueTokenRequest;
import io.project.concertbooking.interfaces.api.v1.queue.response.IssueQueueTokenResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {

    @PostMapping("/tokens")
    public ApiResponse<?> issueQueueToken(@RequestBody IssueQueueTokenRequest request) {
        return ApiResponse.ok(IssueQueueTokenResponse.builder()
                .token("123e4567-e89b-12d3-a456-426614174000")
                .build());
    }

}

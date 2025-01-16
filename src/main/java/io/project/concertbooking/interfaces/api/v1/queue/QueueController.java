package io.project.concertbooking.interfaces.api.v1.queue;

import io.project.concertbooking.application.queue.QueueFacade;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.queue.request.IssueQueueTokenRequest;
import io.project.concertbooking.interfaces.api.v1.queue.response.mapper.QueueResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "큐(대기열)")
@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;
    private final QueueResponseMapper responseMapper;

    @Operation(summary = "대기열 토큰 발급", description = "콘서트 예약을 위한 대기열 토큰을 발급합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "발급 성공")
    })
    @PostMapping("/tokens")
    public ApiResponse<?> issueQueueToken(@RequestBody IssueQueueTokenRequest request) {
        return ApiResponse.ok(responseMapper.toQueueResponse(queueFacade.issueQueueToken(request.getUserId())));
    }

    @Operation(summary = "대기열 조회", description = "대기열의 상태를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ApiResponse<?> getQueueStatus(
            @RequestHeader("Queue-Token") @Parameter(description = "대기열 토큰", example = "123e4567-e89b-12d3-a456-426614174000") String queueToken
    ) {
        return ApiResponse.ok(responseMapper.toQueueStatusResponse(queueFacade.findQueueStatus(queueToken)));
    }
}

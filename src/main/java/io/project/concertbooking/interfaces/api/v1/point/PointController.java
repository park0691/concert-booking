package io.project.concertbooking.interfaces.api.v1.point;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.point.request.ChargePointRequest;
import io.project.concertbooking.interfaces.api.v1.point.response.ChargePointResponse;
import io.project.concertbooking.interfaces.api.v1.point.response.GetPointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트")
@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    @Operation(summary = "포인트 잔액 충전", description = "사용자의 포인트 잔액을 충전합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "충전 성공")
    })
    @PostMapping("/charge")
    public ApiResponse<?> chargeAmount(@RequestBody ChargePointRequest chargePointRequest) {
        return ApiResponse.ok(ChargePointResponse.builder()
                .currentPointAmount(12345)
                .build());
    }

    @Operation(summary = "포인트 잔액 조회", description = "사용자의 포인트 잔액을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/user/{userId}")
    public ApiResponse<?> getAmount(
            @PathVariable("userId") @Parameter(description = "사용자 ID", example = "1") Long userId
    ) {
        return ApiResponse.ok(GetPointResponse.builder()
                .pointAmount(10000)
                .build());
    }
}
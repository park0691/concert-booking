package io.project.concertbooking.interfaces.api.v1.point;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.point.request.ChargePointRequest;
import io.project.concertbooking.interfaces.api.v1.point.request.GetPointRequest;
import io.project.concertbooking.interfaces.api.v1.point.response.ChargePointResponse;
import io.project.concertbooking.interfaces.api.v1.point.response.GetPointResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/points")
public class PointController {

    @PostMapping("/charge")
    public ApiResponse<?> chargeAmount(@RequestBody ChargePointRequest chargePointRequest) {
        return ApiResponse.ok(ChargePointResponse.builder()
                .currentPointAmount(12345)
                .build());
    }

    @GetMapping
    public ApiResponse<?> getAmount(@RequestBody GetPointRequest getPointRequest) {
        return ApiResponse.ok(GetPointResponse.builder()
                .pointAmount(10000)
                .build());
    }
}
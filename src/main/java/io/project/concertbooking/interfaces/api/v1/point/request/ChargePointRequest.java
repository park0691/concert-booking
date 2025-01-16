package io.project.concertbooking.interfaces.api.v1.point.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "ChargePointRequest", description = "포인트 충전 요청")
@Getter
public class ChargePointRequest {
    @Schema(description = "사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "충전 포인트", example = "11000")
    private Integer point;

    @Builder
    private ChargePointRequest(Long userId, Integer point) {
        this.userId = userId;
        this.point = point;
    }
}

package io.project.concertbooking.interfaces.api.v1.concert.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "ReserveSeatRequest", description = "좌석 예약 요청")
@Getter
public class ReserveSeatRequest {
    @Schema(description = "사용자 ID", example = "2")
    private Long userId;
}

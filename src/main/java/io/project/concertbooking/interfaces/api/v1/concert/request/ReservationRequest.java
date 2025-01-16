package io.project.concertbooking.interfaces.api.v1.concert.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "ReserveSeatRequest", description = "좌석 예약 요청")
@Getter
public class ReservationRequest {
    @Schema(description = "사용자 ID", example = "2")
    private Long userId;

    @Schema(description = "좌석 ID")
    private List<Long> seatIds;

    @Builder
    private ReservationRequest(Long userId, List<Long> seatIds) {
        this.userId = userId;
        this.seatIds = seatIds;
    }
}

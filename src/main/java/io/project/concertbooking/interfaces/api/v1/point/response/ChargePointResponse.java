package io.project.concertbooking.interfaces.api.v1.point.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChargePointResponse {
    private Integer currentPointAmount;

    @Builder
    private ChargePointResponse(Integer currentPointAmount) {
        this.currentPointAmount = currentPointAmount;
    }
}

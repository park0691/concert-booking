package io.project.concertbooking.interfaces.api.v1.point.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPointResponse {
    private Integer pointAmount;

    @Builder
    private GetPointResponse(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }
}

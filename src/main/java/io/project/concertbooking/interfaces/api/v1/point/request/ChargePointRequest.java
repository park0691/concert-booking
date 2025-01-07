package io.project.concertbooking.interfaces.api.v1.point.request;

import lombok.Getter;

@Getter
public class ChargePointRequest {
    private Long userId;
    private Integer point;
}

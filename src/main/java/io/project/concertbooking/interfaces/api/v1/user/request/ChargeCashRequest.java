package io.project.concertbooking.interfaces.api.v1.user.request;

import lombok.Getter;

@Getter
public class ChargeCashRequest {
    private Long userId;
    private Integer cash;
}

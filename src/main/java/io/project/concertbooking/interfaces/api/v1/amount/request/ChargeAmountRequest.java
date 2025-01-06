package io.project.concertbooking.interfaces.api.v1.amount.request;

import lombok.Getter;

@Getter
public class ChargeAmountRequest {
    private Long userId;
    private Integer cash;
}

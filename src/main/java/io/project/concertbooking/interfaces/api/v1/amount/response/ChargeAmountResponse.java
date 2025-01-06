package io.project.concertbooking.interfaces.api.v1.amount.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChargeAmountResponse {
    private Integer currentAmount;

    @Builder
    private ChargeAmountResponse(Integer currentAmount) {
        this.currentAmount = currentAmount;
    }
}

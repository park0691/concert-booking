package io.project.concertbooking.interfaces.api.v1.amount.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetAmountResponse {
    private Integer amount;

    @Builder
    private GetAmountResponse(Integer amount) {
        this.amount = amount;
    }
}

package io.project.concertbooking.interfaces.api.v1.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GetCashResponse {
    private Integer cash;

    @Builder
    private GetCashResponse(Integer cash) {
        this.cash = cash;
    }
}

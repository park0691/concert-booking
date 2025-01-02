package io.project.concertbooking.interfaces.api.v1.concert.request;

import lombok.Getter;

@Getter
public class PaySeatRequest {
    private Long userId;
    private Integer price;
}

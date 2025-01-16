package io.project.concertbooking.application.concert.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatResult {
    private Long seatId;
    private Integer number;
    private Integer price;
}

package io.project.concertbooking.interfaces.api.v1.concert.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class SeatResponse {

    private List<Info> seats;

    @Getter
    @Setter
    public static class Info {
        private Long seatId;
        private Integer number;
        private Integer price;

        @Builder
        private Info(Long seatId, Integer number, Integer price) {
            this.seatId = seatId;
            this.number = number;
            this.price = price;
        }
    }

    @Builder
    private SeatResponse(List<Info> seats) {
        this.seats = seats;
    }

    public static SeatResponse of(List<Info> seats) {
        return SeatResponse.builder()
                .seats(seats)
                .build();
    }
}
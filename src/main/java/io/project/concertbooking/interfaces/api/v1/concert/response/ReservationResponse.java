package io.project.concertbooking.interfaces.api.v1.concert.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReservationResponse {

    private final List<Info> reservations;

    @Getter
    @Setter
    public static class Info {
        private Long concertScheduleId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scheduleDt;
        private Long reservationId;
        private Integer seatNumber;
        private Integer price;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expirationDt;
    }

    @Builder
    private ReservationResponse(List<Info> reservations) {
        this.reservations = reservations;
    }

    public static ReservationResponse of(List<Info> reservations) {
        return ReservationResponse.builder()
                .reservations(reservations)
                .build();
    }
}

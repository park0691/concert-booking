package io.project.concertbooking.domain.seat.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SeatReservationStatus {
    RESERVED("R", "예약"),
    CONFIRMED("C", "확정"),
    EXPIRED("X", "예약 만료");

    private final String dbCode;
    private final String label;

    SeatReservationStatus(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static SeatReservationStatus fromDbCode(String dbCode) {
        return Arrays.stream(SeatReservationStatus.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 상태 %s가 존재하지 않습니다.", dbCode)));
    }
}

package io.project.concertbooking.domain.seat.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SeatStatus {
    EMPTY("E", "빈 좌석"),
    RESERVED("R", "예약 좌석"),
    OCCUPIED("O", "확정 좌석");

    private final String dbCode;
    private final String label;

    SeatStatus(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static SeatStatus fromDbCode(String dbCode) {
        return Arrays.stream(SeatStatus.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("좌석 상태 %s가 존재하지 않습니다.", dbCode)));
    }
}

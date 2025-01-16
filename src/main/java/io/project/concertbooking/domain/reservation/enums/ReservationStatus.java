package io.project.concertbooking.domain.reservation.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReservationStatus {
    RESERVED("R", "예약(결제 대기)"),
    PAID("P", "결제 완료(확정)"),
    EXPIRED("X", "예약 만료");

    private final String dbCode;
    private final String label;

    ReservationStatus(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static ReservationStatus fromDbCode(String dbCode) {
        return Arrays.stream(ReservationStatus.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("예약 상태 %s가 존재하지 않습니다.", dbCode)));
    }
}

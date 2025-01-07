package io.project.concertbooking.domain.payment.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentStatus {
    SUCCESS("S", "성공"),
    FAIL("F", "실패");

    private final String dbCode;
    private final String label;

    PaymentStatus(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static PaymentStatus fromDbCode(String dbCode) {
        return Arrays.stream(PaymentStatus.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("결제 상태 %s가 존재하지 않습니다.", dbCode)));
    }
}

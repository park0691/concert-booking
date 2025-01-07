package io.project.concertbooking.domain.payment.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentMethod {
    CASH("CA", "캐시"),
    NAVER_PAY("NA", "네이버페이");

    private final String dbCode;
    private final String label;

    PaymentMethod(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static PaymentMethod fromDbCode(String dbCode) {
        return Arrays.stream(PaymentMethod.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("결제 수단 %s가 존재하지 않습니다.", dbCode)));
    }
}

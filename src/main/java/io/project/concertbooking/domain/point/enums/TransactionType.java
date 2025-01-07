package io.project.concertbooking.domain.point.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionType {
    CHARGE("C", "충전"),
    USE("U", "사용");

    private final String dbCode;
    private final String label;

    TransactionType(final String dbCode, final String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static TransactionType fromDbCode(String dbCode) {
        return Arrays.stream(TransactionType.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("거래 유형 %s가 존재하지 않습니다.", dbCode)));
    }
}

package io.project.concertbooking.domain.queue.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum QueueStatus {
    WAITING("W", "대기"),
    ACTIVATED("A", "활성화"),
    EXPIRED("X", "만료");

    private final String dbCode;
    private final String label;

    QueueStatus(String dbCode, String label) {
        this.dbCode = dbCode;
        this.label = label;
    }

    public static QueueStatus fromDbCode(String dbCode) {
        return Arrays.stream(QueueStatus.values())
                .filter(v -> v.getDbCode().equals(dbCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("대기열 상태 %s가 존재하지 않습니다.", dbCode)));
    }
}

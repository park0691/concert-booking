package io.project.concertbooking.domain.queue;

import io.project.concertbooking.domain.queue.enums.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Queue - 단위 테스트]")
class QueueTest {

    @DisplayName("큐(대기열) 토큰을 만료시킨다.")
    @ParameterizedTest
    @EnumSource(value = QueueStatus.class, names = {"WAITING", "ACTIVATED"})
    void expireQueueToken(QueueStatus status) {
        // given
        Queue queue = Queue.builder()
                .status(status)
                .build();

        // when
        queue.expireQueueToken();

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.EXPIRED);
    }

    @DisplayName("큐(대기열)가 만료 상태인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"WAITING,false", "ACTIVATED,false", "EXPIRED,true"})
    void isExpired(QueueStatus status, boolean expected) {
        // given
        Queue queue = Queue.builder()
                .status(status)
                .build();

        // when
        boolean isExpired = queue.isExpired();

        // then
        assertThat(isExpired).isEqualTo(expected);
    }

    @DisplayName("큐(대기열)가 대기 상태인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"WAITING,true", "ACTIVATED,false", "EXPIRED,false"})
    void isWaiting(QueueStatus status, boolean expected) {
        // given
        Queue queue = Queue.builder()
                .status(status)
                .build();

        // when
        boolean isExpired = queue.isWaiting();

        // then
        assertThat(isExpired).isEqualTo(expected);
    }
}
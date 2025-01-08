package io.project.concertbooking.domain.queue;

import io.project.concertbooking.domain.queue.enums.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

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
}
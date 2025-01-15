package io.project.concertbooking.domain.queue;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.infrastructure.queue.repository.QueueJpaRepository;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class QueueServiceIntegrationTest {

    @Autowired
    QueueService queueService;

    @Autowired
    QueueJpaRepository queueJpaRepository;

    @Autowired
    IQueueRepository queueRepository;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @BeforeEach
    void beforeEach() {
        queueJpaRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("dequeueByExpiringOutdatedToken() 테스트")
    class DequeueByExpiringOutdatedTokenTest {

        @DisplayName("유효 시간이 지난 토큰을 만료시킨다.")
        @ParameterizedTest
        @EnumSource(value = QueueStatus.class, names = {"WAITING", "ACTIVATED"})
        void dequeueByExpiringOutdatedToken(QueueStatus status) {
            // given
            LocalDateTime now = LocalDateTime.now();
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .setNull("queueId")
                    .setNull("user")
                    .set("token", Arbitraries.strings().withCharRange('a', 'z').ofMaxLength(128))
                    .set("status", status)
                    .set("expDt", now.minusDays(1L))
                    .sample();
            Queue savedQueue = queueRepository.save(queue);

            // when
            queueService.dequeueByExpiringOutdatedToken();

            // then
            Optional<Queue> queueOpt = queueRepository.findByToken(queue.getToken());
            assertThat(queueOpt.isPresent()).isTrue();

            Queue resultQueue = queueOpt.get();
            assertThat(resultQueue.getQueueId()).isEqualTo(savedQueue.getQueueId());
            assertThat(resultQueue.getStatus()).isEqualTo(QueueStatus.EXPIRED);
        }

        @DisplayName("유효 시간이 지나지 않은 토큰은 만료되지 않아야 한다.")
        @ParameterizedTest
        @EnumSource(value = QueueStatus.class, names = {"WAITING", "ACTIVATED"})
        void dequeueByExpiringOutdatedTokenWithNotOutdated(QueueStatus status) {
            // given
            LocalDateTime now = LocalDateTime.now();
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .setNull("queueId")
                    .setNull("user")
                    .set("token", Arbitraries.strings().withCharRange('a', 'z').ofMaxLength(128))
                    .set("status", status)
                    .set("expDt", now.plusHours(1L))
                    .sample();
            Queue savedQueue = queueRepository.save(queue);

            // when
            queueService.dequeueByExpiringOutdatedToken();

            // then
            Optional<Queue> queueOpt = queueRepository.findByToken(queue.getToken());
            assertThat(queueOpt.isPresent()).isTrue();

            Queue resultQueue = queueOpt.get();
            assertThat(resultQueue.getQueueId()).isEqualTo(savedQueue.getQueueId());
            assertThat(resultQueue.getStatus()).isEqualTo(status);
        }
    }

    @Nested
    @DisplayName("enqueueByActivatingWaitingToken() 테스트")
    class EequeueByActivatingWaitingToken {

        @DisplayName("활성화된 큐 개수가 큐 최대 크기보다 작은 경우 큐를 활성화시킨다.")
        @Test
        public void activateQueueWithInsufficientActivateToken() {
            // given
            LocalDateTime now = LocalDateTime.now();
            int activeCount = 90;
            int waitingCount = 15;
            createQueues(activeCount, QueueStatus.ACTIVATED, now.plusHours(1L));
            createQueues(waitingCount, QueueStatus.WAITING, now.plusHours(1L));

            // when
            queueService.enqueueByActivatingWaitingToken();

            // then
            Long resultActiveCount = queueRepository.findCountByStatus(QueueStatus.ACTIVATED);
            assertThat(resultActiveCount).isEqualTo(100);
            Long resultWaitingCount = queueRepository.findCountByStatus(QueueStatus.WAITING);
            assertThat(resultWaitingCount).isEqualTo(5);
        }

        @DisplayName("활성화된 큐 개수가 큐 최대 크기와 같은 경우 더 이상 큐는 활성화되지 않는다.")
        @Test
        public void activateQueueWithSufficientActivateToken() {
            // given
            LocalDateTime now = LocalDateTime.now();
            int activeCount = 100;
            int waitingCount = 15;
            createQueues(activeCount, QueueStatus.ACTIVATED, now.plusHours(1L));
            createQueues(waitingCount, QueueStatus.WAITING, now.plusHours(1L));

            // when
            queueService.enqueueByActivatingWaitingToken();

            // then
            Long resultActiveCount = queueRepository.findCountByStatus(QueueStatus.ACTIVATED);
            assertThat(resultActiveCount).isEqualTo(100);
            Long resultWaitingCount = queueRepository.findCountByStatus(QueueStatus.WAITING);
            assertThat(resultWaitingCount).isEqualTo(15);
        }
    }

    private void createQueues(int count, QueueStatus status, LocalDateTime expDt) {
        for (int i = 0; i < count; i++) {
            queueRepository.save(
                    fixtureMonkey.giveMeBuilder(Queue.class)
                            .setNull("queueId")
                            .setNull("user")
                            .set("token", Arbitraries.strings().withCharRange('a', 'z').ofMaxLength(128))
                            .set("status", status)
                            .set("expDt", expDt)
                            .sample()
            );
        }
    }
}

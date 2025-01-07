package io.project.concertbooking.domain.queue;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    IQueueRepository queueRepository;

    @InjectMocks
    QueueService queueService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @Nested
    @DisplayName("findByIdAndStatusWithNull 테스트")
    class FindByIdAndStatusTest {

        @DisplayName("존재하지 않는 사용자 ID, 상태로 Queue를 조회하면 빈 Optional을 반환한다.")
        @Test
        void findByIdAndStatusWithEmptyId() {
            // given
            Long userId = 1L;

            // when
            Optional<Queue> queueOpt = queueService.findByIdAndStatus(userId, QueueStatus.WAITING);

            // then
            assertThat(queueOpt.isPresent()).isFalse();
        }

        @DisplayName("존재하는 사용자 ID, 상태로 Queue를 조회하면 Null이 아닌 Optional<Queue>를 반환한다.")
        @Test
        void findByIdAndStatus() {
            // given
            Queue queue = fixtureMonkey.giveMeOne(Queue.class);
            given(queueService.findByIdAndStatus(any(Long.class), any(QueueStatus.class)))
                    .willReturn(Optional.of(queue));

            // when
            Optional<Queue> queueOpt = queueService.findByIdAndStatus(1L, QueueStatus.WAITING);

            // then
            assertThat(queueOpt.isPresent()).isTrue();
            assertThat(queueOpt.get()).usingRecursiveComparison().isEqualTo(queue);
        }

    }

    @DisplayName("큐 토큰을 만료시킨다.")
    @Test
    void expire() {
        // given
        Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                .set("status", QueueStatus.WAITING)
                .sample();

        // when
        queueService.expire(queue);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.EXPIRED);
    }

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createQueueToken() {
        // given
        User user = fixtureMonkey.giveMeOne(User.class);
        Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                .set("user", user)
                .sample();
        given(queueRepository.save(any(Queue.class)))
                .willReturn(queue);

        // when
        Queue resultQueue = queueService.createQueueToken(user);

        // then
        assertThat(resultQueue).isNotNull();
        assertThat(resultQueue).usingRecursiveComparison().isEqualTo(queue);
    }
}
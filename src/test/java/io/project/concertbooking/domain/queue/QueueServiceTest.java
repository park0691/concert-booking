package io.project.concertbooking.domain.queue;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("findByIdAndStatus() 테스트")
    class FindByIdAndStatusTest {

        @DisplayName("존재하지 않는 사용자, 상태로 대기열을 조회하면 빈 Optional을 반환한다.")
        @Test
        void findByIdAndStatusWithEmptyId() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);

            // when
            Optional<Queue> queueOpt = queueService.findByUserAndStatus(user, QueueStatus.WAITING);

            // then
            assertThat(queueOpt.isPresent()).isFalse();
        }

        @DisplayName("존재하는 사용자, 상태로 대기열을 조회하면 실제 대기열을 담은 Optional<Queue>를 반환한다.")
        @Test
        void findByIdAndStatus() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .set("user", user)
                    .sample();
            given(queueService.findByUserAndStatus(any(User.class), any(QueueStatus.class)))
                    .willReturn(Optional.of(queue));

            // when
            Optional<Queue> queueOpt = queueService.findByUserAndStatus(user, QueueStatus.WAITING);

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

    @Nested
    @DisplayName("findByToken() 테스트")
    class FindByTokenTest {

        @DisplayName("존재하지 않는 토큰으로 대기열을 조회하면 예외가 발생한다.")
        @Test
        void findByTokenWithEmptyToken() {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";

            // when, then
            assertThatThrownBy(() -> queueService.findByToken(token))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_NOT_FOUND);
        }

        @DisplayName("존재하는 토큰으로 대기열을 조회한다.")
        @Test
        void findByToken() {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .set("token", Values.just(token))
                    .sample();
            given(queueRepository.findByToken(any(String.class)))
                    .willReturn(Optional.of(queue));

            // when
            Optional<Queue> queueOpt = queueRepository.findByToken(token);

            // then
            assertThat(queueOpt.isPresent()).isTrue();
            assertThat(queueOpt.get()).usingRecursiveComparison().isEqualTo(queue);
        }

    }

    @DisplayName("입력된 대기열 ID 보다 작은 대기 중인 대기열의 수를 반환한다.")
    @Test
    void findWaitingCount() {
        // given
        given(queueRepository.findCountByIdAndStatus(any(Long.class), any(QueueStatus.class)))
                .willReturn(12);

        // when
        Integer waitingCount = queueService.findWaitingCount(1L);

        // then
        assertThat(waitingCount).isNotNull();
        assertThat(waitingCount).isEqualTo(12);
    }
}
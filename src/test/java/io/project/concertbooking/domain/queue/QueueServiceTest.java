package io.project.concertbooking.domain.queue;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.constants.QueueConstants;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    IQueueRepository queueRepository;

    @Mock
    QueueConstants queueConstants;

    @InjectMocks
    QueueService queueService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @Nested
    @DisplayName("findByUserAndStatus() 테스트")
    class FindByUserAndStatusest {

        @DisplayName("존재하지 않는 사용자, 상태로 큐(대기열)를 조회하면 빈 Optional을 반환한다.")
        @Test
        void findByUserAndStatusWithEmptyId() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);

            // when
            Optional<Queue> queueOpt = queueService.findByUserAndStatus(user, QueueStatus.WAITING);

            // then
            assertThat(queueOpt.isPresent()).isFalse();
        }

        @DisplayName("존재하는 사용자, 상태로 대기열을 조회하면 실제 큐(대기열)를 담은 Optional<Queue>를 반환한다.")
        @Test
        void findByUserAndStatus() {
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

    @DisplayName("큐(대기열) 토큰을 만료시킨다.")
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

    @DisplayName("큐(대기열) 토큰을 생성한다.")
    @Test
    void createQueueToken() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = fixtureMonkey.giveMeOne(User.class);
        Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                .set("user", user)
                .sample();
        given(queueRepository.save(any(Queue.class)))
                .willReturn(queue);

        // when
        String token = queueService.createQueueToken(user, now);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Nested
    @DisplayName("findByToken() 테스트")
    class FindByTokenTest {

        @DisplayName("존재하지 않는 토큰으로 큐(대기열)를 조회하면 예외가 발생한다.")
        @Test
        void findByTokenWithEmptyToken() {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";

            // when, then
            assertThatThrownBy(() -> queueService.findByToken(token))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_NOT_FOUND);
        }

        @DisplayName("존재하는 토큰으로 큐(대기열)를 조회한다.")
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
            Queue foundQueue = queueService.findByToken(token);

            // then
            assertThat(foundQueue).usingRecursiveComparison().isEqualTo(queue);
            verify(queueRepository, times(1)).findByToken(eq(token));
        }
    }

    @DisplayName("입력된 큐(대기열) ID를 통해 대기 순번을 반환한다.")
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

    @Nested
    @DisplayName("validateToken() 테스트")
    class ValidateTokenTest {

        @DisplayName("존재하지 않는 토큰으로 큐(대기열)를 검증하면 예외가 발생한다.")
        @Test
        void validateTokenWithEmptyToken() {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";

            // when, then
            assertThatThrownBy(() -> queueService.validateToken(token))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_NOT_FOUND);
        }

        @DisplayName("활성화 상태가 아닌 토큰의 대기열을 검증하면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = QueueStatus.class, names = {"WAITING", "EXPIRED"})
        void validateTokenWithNotActiveToken(QueueStatus status) {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .set("token", Values.just(token))
                    .set("status", status)
                    .sample();
            given(queueRepository.findByToken(any(String.class)))
                    .willReturn(Optional.of(queue));

            // when, then
            assertThatThrownBy(() -> queueService.validateToken(token))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_NOT_ACTIVE);
        }

        @DisplayName("활성화 상태의 토큰을 검증한다.")
        @Test
        void validateToken() {
            // given
            String token = "550e8400-e29b-41d4-a716-446655440000";
            Queue queue = fixtureMonkey.giveMeBuilder(Queue.class)
                    .set("token", Values.just(token))
                    .set("status", QueueStatus.ACTIVATED)
                    .sample();
            given(queueRepository.findByToken(any(String.class)))
                    .willReturn(Optional.of(queue));

            // when
            queueService.validateToken(token);

            // then
            verify(queueRepository, times(1)).findByToken(token);
        }
    }

    @DisplayName("활성화 가능한 최대 큐 크기만큼 대기열이 활성화되어 있으면 큐 활성화 로직은 작동하지 않는다.")
    @Test
    void enqueueByActivatingWaitingTokenWhenMaxQueueActivated() {
        // given
        given(queueConstants.getMaxSize())
                .willReturn(100);
        given(queueRepository.findCountByStatus(any(QueueStatus.class)))
                .willReturn(100L);

        // when
        queueService.enqueueByActivatingWaitingToken();

        // then
        verify(queueRepository, never()).findAllWaitingLimit(any(Integer.class));
        verify(queueRepository, never()).updateStatusByIds(any(QueueStatus.class), anyList());
    }
}
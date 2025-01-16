package io.project.concertbooking.domain.point;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.point.repository.PointJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[PointService - 동시성 테스트]")
class PointServiceConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    PointService pointService;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    PointJpaRepository pointJpaRepository;

    @Nested
    @DisplayName("[charge() - 포인트 충전 테스트]")
    class ChargeTest {

        @DisplayName("5개의 충전 요청이 동시에 오더라도 5개의 충전 금액이 모두 반영되어야 한다.")
        @RepeatedTest(value = 5, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
        void charge() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            pointJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Point.class)
                            .set("user", Values.just(user))
                            .set("amount", 0)
                            .sample()
            );

            int concurrentRequestCount = 5;
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(concurrentRequestCount);

            // when
            for (int i = 0; i < concurrentRequestCount; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.chargeWithLock(user, 1000);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executorService.shutdown();

            // then
            Optional<Point> pointOpt = pointJpaRepository.findByUser(user);
            assertThat(pointOpt.isPresent()).isTrue();

            Point point = pointOpt.get();
            assertThat(point.getAmount()).isEqualTo(5000);
        }
    }
}
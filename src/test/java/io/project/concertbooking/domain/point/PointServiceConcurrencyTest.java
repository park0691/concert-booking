package io.project.concertbooking.domain.point;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.point.repository.PointJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
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

        @DisplayName("10번의 포인트 충전 요청이 동시에 오더라도 10번의 요청이 모두 반영되어야 한다")
        @RepeatedTest(value = 10, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
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

            int concurrentRequestCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(concurrentRequestCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < concurrentRequestCount; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.chargeWithDistributedLock(user, 1000);
                        successCount.incrementAndGet();
                    } catch (CustomException e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executorService.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(10);
            assertThat(failCount.get()).isEqualTo(0);

            Optional<Point> pointOpt = pointJpaRepository.findByUser(user);
            assertThat(pointOpt.isPresent()).isTrue();

            Point point = pointOpt.get();
            assertThat(point.getAmount()).isEqualTo(10000);
        }
    }

    @Nested
    @DisplayName("[use() - 포인트 사용 테스트]")
    class UseTest {

        @DisplayName("10번의 포인트 사용 요청이 동시에 오더라도 10번의 요청이 모두 반영되어야 한다")
        @RepeatedTest(value = 10, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
        void use() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            pointJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Point.class)
                            .set("user", Values.just(user))
                            .set("amount", 20000)
                            .sample()
            );

            int concurrentRequestCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(concurrentRequestCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < concurrentRequestCount; i++) {
                executorService.submit(() -> {
                    try {
                        pointService.useWithDistributedLock(user, 1000);
                        successCount.incrementAndGet();
                    } catch (CustomException e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executorService.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(10);
            assertThat(failCount.get()).isEqualTo(0);

            Optional<Point> pointOpt = pointJpaRepository.findByUser(user);
            assertThat(pointOpt.isPresent()).isTrue();

            Point point = pointOpt.get();
            assertThat(point.getAmount()).isEqualTo(10000);
        }
    }
}
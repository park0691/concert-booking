package io.project.concertbooking.application.reservation;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.reservation.ReservationJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import net.jqwik.api.Arbitraries;
import net.jqwik.time.api.DateTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[ReservationFacade - 동시성 테스트]")
public class ReservationFacadeConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    ReservationFacade reservationFacade;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Nested
    @DisplayName("[createReservation() - 콘서트 좌석 예약 테스트]")
    class CreateReservationTest {

        @DisplayName("동시에 5개의 예약 요청이 들어오면, 하나만 성공하고 4개는 실패한다.")
        @RepeatedTest(value = 5, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
        void createReservation() throws Exception {
            // given
            User user = userJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(User.class)
                            .sample()
            );
            LocalDateTime now = LocalDateTime.now();
            Concert concert = concertJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Concert.class)
                            .set("name", Arbitraries.strings().withCharRange('A', 'Z').ofLength(10).map(s -> s + " Concert"))
                            .sample()
            );
            ConcertSchedule schedule = concertScheduleJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                            .set("concert", Values.just(concert))
                            .set("scheduleDt", DateTimes.dateTimes().atTheEarliest(now.plusDays(7L))
                                    .atTheLatest(now.plusDays(60L)))
                            .sample()
            );
            Seat seat1 = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 1)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.EMPTY))
                            .sample()
            );
            Seat seat2 = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 2)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.EMPTY))
                            .sample()
            );

            int concurrentRequestCount = 5;
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(concurrentRequestCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < concurrentRequestCount; i++) {
                executorService.submit(() -> {
                    try {
                        reservationFacade.reserve(schedule.getConcertScheduleId(), user.getUserId(),
                                List.of(seat1.getSeatId(), seat2.getSeatId()));
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
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(4);
        }
    }
}

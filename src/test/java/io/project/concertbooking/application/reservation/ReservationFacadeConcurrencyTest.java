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

        @DisplayName("동시에 5개의 동일한 좌석에 대한 예약 요청이 들어오면, 하나만 성공하고 4개는 실패한다.")
        @RepeatedTest(value = 10, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
        void createReservationWithSameRequest() throws Exception {
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

        @DisplayName("동시에 동일한 좌석을 포함한 5개의 예약 요청이 동시에 들어오면, 하나만 성공하고 4개는 실패한다.")
        @RepeatedTest(value = 10, name = "{currentRepetition}/{totalRepetitions} - {displayName}")
        void createReservationWithDiffRequest() throws Exception {
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
            Seat seat3 = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 3)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.EMPTY))
                            .sample()
            );
            Seat seat4 = seatJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Seat.class)
                            .set("concertSchedule", Values.just(schedule))
                            .set("number", 4)
                            .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                            .set("status", Values.just(SeatStatus.EMPTY))
                            .sample()
            );

            int concurrentRequestCount = 5;
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            CountDownLatch latch = new CountDownLatch(concurrentRequestCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            List<Long> seats1 = List.of(seat1.getSeatId(), seat4.getSeatId());
            List<Long> seats2 = List.of(seat2.getSeatId(), seat4.getSeatId());
            List<Long> seats3 = List.of(seat4.getSeatId(), seat3.getSeatId());
            List<Long> seats4 = List.of(seat2.getSeatId(), seat4.getSeatId());
            List<Long> seats5 = List.of(seat1.getSeatId(), seat4.getSeatId());

            // when
            List.of(seats1, seats2, seats3, seats4, seats5).forEach(seats -> {
                executorService.submit(() -> {
                    try {
                        reservationFacade.reserve(schedule.getConcertScheduleId(), user.getUserId(), seats);
                        successCount.incrementAndGet();
                    } catch (CustomException e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            });

            latch.await();
            executorService.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(4);

            List<Seat> reservedSeats = seatJpaRepository.findAllByStatus(SeatStatus.RESERVED);
            assertThat(reservedSeats)
                    .as("5개의 요청 중 1개만 성공했다면 예약된 좌석들이 반환되어야 한다.")
                    .isNotEmpty();
            assertThat(reservedSeats)
                    .as("5개의 요청 중 1개만 성공했다면 예약 상태의 좌석은 2개이어야 한다.")
                    .hasSize(2);
        }

    }
}

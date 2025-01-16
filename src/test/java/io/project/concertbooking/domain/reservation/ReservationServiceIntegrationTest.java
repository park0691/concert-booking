package io.project.concertbooking.domain.reservation;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.reservation.ReservationJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupportWithNoAuditing;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[ReservationService - 통합 테스트]")
class ReservationServiceIntegrationTest extends IntegrationTestSupportWithNoAuditing {

    @Autowired
    ReservationService seatService;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Nested
    @DisplayName("[expireReservation() - 예약 만료 처리 테스트]")
    class ExpireReservationTest {

        @Transactional
        @DisplayName("예약한 후 5분이 지난 예약만 만료 처리되고, 해당 좌석은 빈 상태로 바뀐다.")
        @Test
        void expireReservation() {
            // given
            AtomicInteger seatNumber = new AtomicInteger(0);
            Seat seat1 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .sample();
            Seat seat2 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.RESERVED))
                    .sample();
            Seat seat3 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .sample();
            Seat seat4 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.RESERVED))
                    .sample();
            Seat seat5 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.OCCUPIED))
                    .sample();
            Seat seat6 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.RESERVED))
                    .sample();
            Seat seat7 = fixtureMonkey.giveMeBuilder(Seat.class)
                    .setNull("concertSchedule")
                    .setLazy("number", () -> seatNumber.addAndGet(1))
                    .set("price", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(10).map(n -> n * 10000))
                    .set("status", Values.just(SeatStatus.RESERVED))
                    .sample();
            seatJpaRepository.saveAll(List.of(seat1, seat2, seat3, seat4, seat5, seat6, seat7));

            LocalDateTime now = LocalDateTime.now();
            Reservation reservation1 = fixtureMonkey.giveMeBuilder(Reservation.class)
                    .setNull("user")
                    .set("seat", Values.just(seat2))
                    .set("seatNumber", seat2.getNumber())
                    .set("status", ReservationStatus.RESERVED)
                    .set("regDt", now.minusMinutes(4))
                    .sample();
            Reservation reservation2 = fixtureMonkey.giveMeBuilder(Reservation.class)
                    .setNull("user")
                    .set("seat", Values.just(seat4))
                    .set("seatNumber", seat4.getNumber())
                    .set("status", ReservationStatus.RESERVED)
                    .set("regDt", now.minusMinutes(5))
                    .sample();
            Reservation reservation3 = fixtureMonkey.giveMeBuilder(Reservation.class)
                    .setNull("user")
                    .set("seat", Values.just(seat6))
                    .set("seatNumber", seat6.getNumber())
                    .set("status", ReservationStatus.RESERVED)
                    .set("regDt", now.minusMinutes(6))
                    .sample();
            Reservation reservation4 = fixtureMonkey.giveMeBuilder(Reservation.class)
                    .setNull("user")
                    .set("seat", Values.just(seat7))
                    .set("seatNumber", seat7.getNumber())
                    .set("status", ReservationStatus.RESERVED)
                    .set("regDt", now.minusMinutes(7))
                    .sample();
            reservationJpaRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4));

            // when
            seatService.expireReservation(now);

            // then
            List<Reservation> expiredReservations = reservationJpaRepository.findAllByStatus(ReservationStatus.EXPIRED);
            assertThat(expiredReservations).hasSize(2);
            assertThat(expiredReservations).containsExactlyInAnyOrder(reservation3, reservation4);

            List<Seat> emptySeats = seatJpaRepository.findAllByStatus(SeatStatus.EMPTY);
            assertThat(emptySeats).hasSize(4);
            assertThat(emptySeats).containsExactlyInAnyOrder(seat1, seat3, seat6, seat7);
        }
    }
}
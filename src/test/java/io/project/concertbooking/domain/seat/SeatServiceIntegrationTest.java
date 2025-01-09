package io.project.concertbooking.domain.seat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import io.project.concertbooking.infrastructure.seat.SeatJpaRepository;
import io.project.concertbooking.infrastructure.seat.SeatReservationJpaRepository;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SeatServiceIntegrationTest {

    @Autowired
    SeatService seatService;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    SeatReservationJpaRepository seatReservationJpaRepository;

    @Mock
    private AuditorAware<String> auditorAware;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @BeforeEach
    void beforeEach() {
        seatJpaRepository.deleteAllInBatch();
        seatReservationJpaRepository.deleteAllInBatch();
    }

    @DisplayName("예약한 후 5분이 지난 예약을 만료시킨다.")
    @Test
    void expireReservation() {
        // given
        Seat seat1 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.EMPTY)
                .sample();
        Seat seat2 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.RESERVED)
                .sample();
        Seat seat3 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.EMPTY)
                .sample();
        Seat seat4 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.RESERVED)
                .sample();
        Seat seat5 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.OCCUPIED)
                .sample();
        Seat seat6 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.RESERVED)
                .sample();
        Seat seat7 = fixtureMonkey.giveMeBuilder(Seat.class)
                .setNull("concertSchedule")
                .set("number", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatStatus.RESERVED)
                .sample();
        seatJpaRepository.saveAll(List.of(seat1, seat2, seat3, seat4, seat5, seat6, seat7));

        LocalDateTime now = LocalDateTime.now();
        SeatReservation reservation1 = fixtureMonkey.giveMeBuilder(SeatReservation.class)
                .setNull("user")
                .set("seat", seat2)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(4))
                .sample();
        SeatReservation reservation2 = fixtureMonkey.giveMeBuilder(SeatReservation.class)
                .setNull("user")
                .set("seat", seat4)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(5))
                .sample();
        SeatReservation reservation3 = fixtureMonkey.giveMeBuilder(SeatReservation.class)
                .setNull("user")
                .set("seat", seat6)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(6))
                .sample();
        SeatReservation reservation4 = fixtureMonkey.giveMeBuilder(SeatReservation.class)
                .setNull("user")
                .set("seat", seat6)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", SeatReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(7))
                .sample();
        seatReservationJpaRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4));

        // when
        seatService.expireReservation(now);

        // then
        List<SeatReservation> reservations = seatReservationJpaRepository.findAllByStatus(SeatReservationStatus.EXPIRED);
        assertThat(reservations).hasSize(2);

        List<Seat> seats = seatJpaRepository.findAllByStatus(SeatStatus.EMPTY);
        assertThat(seats).hasSize(4);
    }
}
package io.project.concertbooking.domain.reservation;

import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.infrastructure.concert.repository.SeatJpaRepository;
import io.project.concertbooking.infrastructure.reservation.ReservationJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    ReservationService seatService;

    @Autowired
    SeatJpaRepository seatJpaRepository;

    @Autowired
    ReservationJpaRepository reservationJpaRepository;

    @Mock
    private AuditorAware<String> auditorAware;

    @BeforeEach
    void beforeEach() {
        seatJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();
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
        Reservation reservation1 = fixtureMonkey.giveMeBuilder(Reservation.class)
                .setNull("user")
                .set("seat", seat2)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", ReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(4))
                .sample();
        Reservation reservation2 = fixtureMonkey.giveMeBuilder(Reservation.class)
                .setNull("user")
                .set("seat", seat4)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", ReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(5))
                .sample();
        Reservation reservation3 = fixtureMonkey.giveMeBuilder(Reservation.class)
                .setNull("user")
                .set("seat", seat6)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", ReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(6))
                .sample();
        Reservation reservation4 = fixtureMonkey.giveMeBuilder(Reservation.class)
                .setNull("user")
                .set("seat", seat6)
                .set("seatNumber", Arbitraries.integers().greaterOrEqual(1).lessOrEqual(50))
                .set("status", ReservationStatus.RESERVED)
                .set("regDt", now.minusMinutes(7))
                .sample();
        reservationJpaRepository.saveAll(List.of(reservation1, reservation2, reservation3, reservation4));

        // when
        seatService.expireReservation(now);

        // then
        List<Reservation> reservations = reservationJpaRepository.findAllByStatus(ReservationStatus.EXPIRED);
        assertThat(reservations).hasSize(2);

        List<Seat> seats = seatJpaRepository.findAllByStatus(SeatStatus.EMPTY);
        assertThat(seats).hasSize(4);
    }
}
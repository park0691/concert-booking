package io.project.concertbooking.domain.reservation;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    IReservationRepository reservationRepository;

    @InjectMocks
    SeatService seatService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("좌석이 이미 예약됬거나 확정된 좌석이면 예외가 발생한다.")
    @ParameterizedTest
    @EnumSource(value = SeatStatus.class, names = {"OCCUPIED", "RESERVED"})
    void createReservationWith(SeatStatus status) {
        // given
        User user = fixtureMonkey.giveMeOne(User.class);
        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);
        Seat seat = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", status)
                .sample();

        // when, then
        assertThatThrownBy(() -> seatService.createReservation(user, concertSchedule, seat))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_NOT_AVAILABLE);
    }

    @DisplayName("좌석이 빈 좌석이면 좌석이 예약된다.")
    @Test
    void createReservation() {
        // given
        User user = fixtureMonkey.giveMeOne(User.class);
        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);
        Seat seat = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();

        // when
        seatService.createReservation(user, concertSchedule, seat);

        // then
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @DisplayName("존재하지 않는 예약 아이디로 예약을 조회하면 예외가 발생한다.")
    @Test
    void findReservationWithInvalidReservationId() {
        // given
        Long reservationId = 1L;

        // when, then
        assertThatThrownBy(() -> seatService.findReservation(reservationId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);
    }

    @DisplayName("존재하는 예약 아이디로 예약을 조회한다.")
    @Test
    void findReservation() {
        // given
        Reservation reservation = fixtureMonkey.giveMeOne(Reservation.class);
        given(reservationRepository.findById(any(Long.class)))
                .willReturn(Optional.of(reservation));

        // when
        Reservation foundReservation = seatService.findReservation(1L);

        // then
        assertThat(foundReservation).usingRecursiveComparison()
                .isEqualTo(reservation);
    }

    @DisplayName("예약을 확정처리하면 예약 상태가 확정으로, 좌석 상태는 점유로 변경된다.")
    @Test
    void finalizeReservation() {
        // given
        Seat seat = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.RESERVED)
                .sample();
        Reservation seatReservation = fixtureMonkey.giveMeBuilder(Reservation.class)
                .set("seat", seat)
                .set("status", ReservationStatus.RESERVED)
                .sample();

        // when
        seatService.finalizeReservation(seatReservation);

        // then
        assertThat(seatReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(seatReservation.getSeat().getStatus()).isEqualTo(SeatStatus.OCCUPIED);
    }
}
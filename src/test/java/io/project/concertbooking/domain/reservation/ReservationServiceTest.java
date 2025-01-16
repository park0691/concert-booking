package io.project.concertbooking.domain.reservation;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.*;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.support.UnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("[ReservationService - 단위 테스트]")
class ReservationServiceTest extends UnitTestSupport {

    @Mock
    IReservationRepository reservationRepository;

    @Mock
    IConcertRepository concertRepository;

    @Spy
    ScheduleValidator scheduleValidator;

    @Spy
    SeatValidator seatValidator;

    @InjectMocks
    ReservationService reservationService;

    @Nested
    @DisplayName("[createReservation() - 콘서트 좌석 예약 테스트]")
    class CreateReservationTest {

        @DisplayName("콘서트 예약 가능한 시간이 아니면 예외가 발생한다.")
        @Test
        void createReservationWithUnavailableSchedule() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);
            ConcertSchedule concertSchedule = fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                    .set("scheduleDt", Values.just(LocalDateTime.now().minusDays(2L)))
                    .sample();
            List<Seat> seats = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", SeatStatus.EMPTY)
                    .sampleList(3);

            // when, then
            assertThatThrownBy(() -> reservationService.createReservation(user, concertSchedule, seats))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEDULE_UNAVAILABLE);
        }

        @DisplayName("좌석이 이미 예약됬거나 확정된 좌석이면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = SeatStatus.class, names = {"OCCUPIED", "RESERVED"})
        void createReservationWithUnavailableSeat(SeatStatus status) {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);
            ConcertSchedule concertSchedule = fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                    .set("scheduleDt", Values.just(LocalDateTime.now().plusDays(2L)))
                    .sample();
            List<Seat> seats = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", status)
                    .sampleList(3);

            // when, then
            assertThatThrownBy(() -> reservationService.createReservation(user, concertSchedule, seats))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_UNAVAILABLE);
        }

        @DisplayName("스케줄, 좌석 예약 상태 겁증을 통과하면 예약을 생성한다.")
        @Test
        void createReservation() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);
            ConcertSchedule concertSchedule = fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                    .set("scheduleDt", Values.just(LocalDateTime.now().plusDays(2L)))
                    .sample();
            List<Seat> seats = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", SeatStatus.EMPTY)
                    .sampleList(3);

            // when
            reservationService.createReservation(user, concertSchedule, seats);

            // then
            verify(reservationRepository, times(seats.size())).save(any(Reservation.class));
        }
    }

    @Nested
    @DisplayName("[findReservation() - 예약 조회 테스트]")
    class FindReservationTest {

        @DisplayName("존재하지 않는 예약 아이디로 예약을 조회하면 예외가 발생한다.")
        @Test
        void findReservationWithInvalidReservationId() {
            // given
            Long reservationId = 1L;

            // when, then
            assertThatThrownBy(() -> reservationService.findReservation(reservationId))
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
            Reservation foundReservation = reservationService.findReservation(1L);

            // then
            assertThat(foundReservation).usingRecursiveComparison()
                    .isEqualTo(reservation);
        }
    }

    @Nested
    @DisplayName("[finalizeReservation() - 예약 확정 처리 테스트]")
    class FinalizeReservationTest {

        @DisplayName("예약을 확정 처리하면 예약 상태는 확정으로, 좌석 상태는 점유로 변경된다.")
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
            reservationService.finalizeReservation(seatReservation);

            // then
            assertThat(seatReservation.getStatus()).isEqualTo(ReservationStatus.PAID);
            assertThat(seatReservation.getSeat().getStatus()).isEqualTo(SeatStatus.OCCUPIED);
        }
    }
}
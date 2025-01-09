package io.project.concertbooking.domain.seat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import io.project.concertbooking.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    ISeatRepository seatRepository;

    @InjectMocks
    SeatService seatService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("콘서트 스케줄을 통해 예약 가능한(빈) 좌석만 조회한다.")
    @Test
    void findAvailableSeats() {
        // given
        Seat seat1 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.OCCUPIED)
                .sample();
        Seat seat2 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.RESERVED)
                .sample();
        Seat seat3 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();
        Seat seat4 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.OCCUPIED)
                .sample();
        Seat seat5 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();

        given(seatRepository.findAllByConcertSchedule(any(ConcertSchedule.class)))
                .willReturn(List.of(seat1, seat2, seat3, seat4, seat5));

        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);

        // when
        List<Seat> seats = seatService.findAvailableSeats(concertSchedule);

        // then
        assertThat(seats).isNotEmpty();
        assertThat(seats).hasSize(2);
        assertThat(seats).containsExactly(seat3, seat5);
    }

    @DisplayName("존재하지 않는 좌석 아이디로 좌석을 조회하면 예외가 발생한다.")
    @Test
    void findByIdWithInvalidSeatId() {
        // given
        Long seatID = 1L;

        // when, then
        assertThatThrownBy(() -> seatService.findById(1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_NOT_FOUND);
    }

    @DisplayName("존재하는 좌석 아이디로 좌석을 조회한다.")
    @Test
    void findById() {
        // given
        Seat seat = fixtureMonkey.giveMeOne(Seat.class);
        given(seatRepository.findById(any(Long.class)))
                .willReturn(Optional.of(seat));

        // when
        Seat foundSeat = seatService.findById(1L);

        // then
        assertThat(foundSeat).usingRecursiveComparison()
                .isEqualTo(seat);
    }

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
        verify(seatRepository, times(1)).saveReservation(any(SeatReservation.class));
    }
}
package io.project.concertbooking.domain.concert;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    IConcertRepository concertRepository;

    @InjectMocks
    ConcertService concertService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("존재하지 않는 콘서트 아이디로 조회하면 예외가 발생한다.")
    @Test
    void findByIdWithInvalidConcertId() {
        // given
        Long concertId = 1L;

        // when, then
        assertThatThrownBy(() -> concertService.findById(concertId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);
    }

    @DisplayName("존재하는 콘서트 아이디로 콘서트를 조회한다.")
    @Test
    void findById() {
        // given
        Concert concert = fixtureMonkey.giveMeOne(Concert.class);
        given(concertRepository.findById(any(Long.class)))
                .willReturn(Optional.of(concert));

        // when
        Concert foundConcert = concertService.findById(1L);

        // then
        assertThat(foundConcert).usingRecursiveComparison()
                .isEqualTo(concert);
    }

    @DisplayName("존재하지 않는 스케줄 아이디로 콘서트 스케줄을 조회하면 예외가 발생한다.")
    @Test
    void findScheduleByIdWithInvalidScheduleId() {
        // given
        Long scheduleId = 1L;

        // when, then
        assertThatThrownBy(() -> concertService.findScheduleById(scheduleId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEDULE_NOT_FOUND);
    }

    @DisplayName("존재하는 스케줄 아이디로 콘서트 스케줄을 조회한다.")
    @Test
    void findScheduleById() {
        // given
        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);
        given(concertRepository.findScheduleById(any(Long.class)))
                .willReturn(Optional.of(concertSchedule));

        // when
        ConcertSchedule foundSchedule = concertService.findScheduleById(1L);

        // then
        assertThat(foundSchedule).usingRecursiveComparison()
                .isEqualTo(concertSchedule);
    }

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

        given(concertRepository.findAllByConcertSchedule(any(ConcertSchedule.class)))
                .willReturn(List.of(seat1, seat2, seat3, seat4, seat5));

        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);

        // when
        List<Seat> seats = concertService.findAvailableSeats(concertSchedule);

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
        assertThatThrownBy(() -> concertService.findSeatById(1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_NOT_FOUND);
    }

    @DisplayName("존재하는 좌석 아이디로 좌석을 조회한다.")
    @Test
    void findSeatById() {
        // given
        Seat seat = fixtureMonkey.giveMeOne(Seat.class);
        given(concertRepository.findSeatById(any(Long.class)))
                .willReturn(Optional.of(seat));

        // when
        Seat foundSeat = concertService.findSeatById(1L);

        // then
        assertThat(foundSeat).usingRecursiveComparison()
                .isEqualTo(seat);
    }
}
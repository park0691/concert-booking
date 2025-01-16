package io.project.concertbooking.domain.concert;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.support.UnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("[ConcertService - 단위 테스트]")
class ConcertServiceTest extends UnitTestSupport {

    @Mock
    IConcertRepository concertRepository;

    @InjectMocks
    ConcertService concertService;

    @Nested
    @DisplayName("[findById() - 콘서트 정보 조회 테스트]")
    class FindByIdTest {

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
    }

    @Nested
    @DisplayName("[findScheduleById() - 콘서트 스케줄 조회 테스트]")
    class FindScheduleByIdTest {

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
    }

    @Nested
    @DisplayName("[findAvailableSeats() - 예약 가능한(빈) 좌석 조회 테스트]")
    class FindAvailableSeatsTest {

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
    }

    @Nested
    @DisplayName("[findSeats() - 여러 좌석 조회 테스트]")
    class FindSeatsTest {

        @DisplayName("콘재하지 않는 좌석 아이디를 포함해 좌석을 조회하면 예외가 발생한다.")
        @Test
        void findSeatsWithInvalidSeatId() {
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
            given(concertRepository.findSeats(any(List.class)))
                    .willReturn(List.of(seat1, seat2, seat3, seat4));

            // when, then
            assertThatThrownBy(() -> concertService.findSeats(List.of(1L, 2L, 3L, 4L, 5L, 6L)))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_NOT_FOUND);
        }

        @DisplayName("존재하는 좌석 아이디들로 좌석을 조회한다.")
        @Test
        void findSeatById() {
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
            given(concertRepository.findSeats(any(List.class)))
                    .willReturn(List.of(seat1, seat2, seat3, seat4));

            // when, then
            assertThatCode(() -> concertService.findSeats(List.of(1L, 2L, 3L, 4L)))
                    .doesNotThrowAnyException();
        }
    }
}
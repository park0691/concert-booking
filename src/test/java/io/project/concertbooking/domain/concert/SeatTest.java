package io.project.concertbooking.domain.concert;

import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.support.UnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Seat - 단위 테스트]")
class SeatTest extends UnitTestSupport {

    @DisplayName("좌석을 예약 상태로 변경한다.")
    @Test
    void reserve() {
        // given
        Seat seat = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();

        // when
        seat.reserve();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.RESERVED);
    }

    @DisplayName("좌석을 확정 상태로 변경한다.")
    @Test
    void occupy() {
        // given
        Seat seat = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();

        // when
        seat.occupy();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.OCCUPIED);
    }

    @DisplayName("좌석이 예약 상태인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"EMPTY,false", "RESERVED,true", "OCCUPIED,false"})
    void isReserved(SeatStatus status, boolean expected) {
        // given
        Seat seat = Seat.builder()
                .status(status)
                .build();

        // when, then
        assertThat(seat.isReserved()).isEqualTo(expected);
    }

    @DisplayName("좌석이 확정 상태인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"EMPTY,false", "RESERVED,false", "OCCUPIED,true"})
    void isOccupied(SeatStatus status, boolean expected) {
        // given
        Seat seat = Seat.builder()
                .status(status)
                .build();

        // when, then
        assertThat(seat.isOccupied()).isEqualTo(expected);
    }

    @DisplayName("좌석이 빈 상태인지 확인한다.")
    @ParameterizedTest
    @CsvSource({"EMPTY,true", "RESERVED,false", "OCCUPIED,false"})
    void isEmpty(SeatStatus status, boolean expected) {
        // given
        Seat seat = Seat.builder()
                .status(status)
                .build();

        // when, then
        assertThat(seat.isEmpty()).isEqualTo(expected);
    }
}
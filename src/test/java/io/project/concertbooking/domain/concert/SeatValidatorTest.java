package io.project.concertbooking.domain.concert;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.support.UnitTestSupport;
import net.jqwik.api.Arbitrary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[SeatValidator - 단위 테스트]")
class SeatValidatorTest extends UnitTestSupport {

    @InjectMocks
    SeatValidator seatValidator;

    @Nested
    @DisplayName("[checkIfSeatsReservable() - 좌석 예약 가능 검증 테스트]")
    class CheckIfSeatsReservableTest {

        @DisplayName("확정(점유) 또는 예약된 좌석이 입력되면 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = SeatStatus.class, names = {"RESERVED", "OCCUPIED"})
        void checkIfSeatsReservableWithUnavailableSeat(SeatStatus status) {
            // given
            Arbitrary<Seat> seatEmptyArb = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .build();
            Arbitrary<Seat> seatUnavailableArb = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", Values.just(status))
                    .build();
            List<Seat> seats = seatEmptyArb.list().ofSize(3).sample();
            seats.addAll(seatUnavailableArb.list().ofSize(3).sample());

            // when, then
            assertThatThrownBy(() -> seatValidator.checkIfSeatsReservable(seats))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SEAT_UNAVAILABLE);
        }

        @DisplayName("확정(점유), 예약되지 않은 빈 좌석이 입력되면 검증을 통과한다.")
        @Test
        void checkIfSeatsReservable() {
            // given
            Arbitrary<Seat> seatEmptyArb = fixtureMonkey.giveMeBuilder(Seat.class)
                    .set("status", Values.just(SeatStatus.EMPTY))
                    .build();
            List<Seat> seats = seatEmptyArb.list().ofSize(6).sample();

            // when, then
            assertThatCode(() -> seatValidator.checkIfSeatsReservable(seats))
                    .doesNotThrowAnyException();
        }
    }
}
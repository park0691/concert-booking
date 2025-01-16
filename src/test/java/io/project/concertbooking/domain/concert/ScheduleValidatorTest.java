package io.project.concertbooking.domain.concert;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.support.UnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("[ScheduleValidator - 단위 테스트]")
class ScheduleValidatorTest extends UnitTestSupport {

    @InjectMocks
    ScheduleValidator scheduleValidator;

    @Nested
    @DisplayName("[checkIfReservable() - 예약 가능 시간대 검증 테스트]")
    class CheckIfReservableTest {

        @DisplayName("입력된 시간이 콘서트가 열리는 일자의 0시 0분 이후가 아니면 검증을 통과한다.")
        @Test
        void checkIfReservable() {
            // given
            LocalDateTime reservationTime = LocalDateTime.of(2025, Month.AUGUST, 29, 19, 0);
            ConcertSchedule concertSchedule = fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                    .set("scheduleDt", LocalDateTime.of(2025, Month.AUGUST, 30, 18, 0))
                    .sample();

            // when, then
            assertThatCode(() -> scheduleValidator.checkIfReservable(concertSchedule, reservationTime))
                    .doesNotThrowAnyException();
        }

        @DisplayName("입력된 시간이 콘서트가 열리는 일자의 0시 0분 이후이면 예외가 발생한다.")
        @Test
        void checkIfReservableWithAfterMidnightTimeOfScheduleDt() {
            // given
            LocalDateTime reservationTime = LocalDateTime.of(2025, Month.AUGUST, 30, 0, 1);
            ConcertSchedule concertSchedule = fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                    .set("scheduleDt", LocalDateTime.of(2025, Month.AUGUST, 30, 18, 0))
                    .sample();

            // when, then
            assertThatThrownBy(() -> scheduleValidator.checkIfReservable(concertSchedule, reservationTime))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEDULE_UNAVAILABLE);
        }
    }
}
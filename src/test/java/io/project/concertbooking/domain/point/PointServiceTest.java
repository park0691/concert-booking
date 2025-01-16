package io.project.concertbooking.domain.point;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.support.UnitTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("[PointService - 단위 테스트]")
class PointServiceTest extends UnitTestSupport {

    @Mock
    IPointRepository pointRepository;

    @InjectMocks
    PointService pointService;

    @Nested
    @DisplayName("[findPoint() - 포인트 조회 테스트]")
    class FindPointTest {

        @DisplayName("포인트가 조회되지 않는 경우 예외가 발생한다.")
        @Test
        void findPointWithEmptyResult() {
            // given
            User user = fixtureMonkey.giveMeOne(User.class);

            // when, then
            assertThatThrownBy(() -> pointService.findPoint(user))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POINT_NOT_FOUND);
        }

        @DisplayName("포인트를 조회한다.")
        @Test
        void findPoint() {
            // given
            Point point = fixtureMonkey.giveMeOne(Point.class);
            User user = fixtureMonkey.giveMeOne(User.class);
            given(pointRepository.findByUser(any(User.class)))
                    .willReturn(Optional.of(point));

            // when
            Point foundPoint = pointService.findPoint(user);

            // then
            assertThat(foundPoint).usingRecursiveComparison()
                    .isEqualTo(foundPoint);
        }
    }

    @Nested
    @DisplayName("[use() - 포인트 차감 테스트]")
    class UseTest {

        @DisplayName("포인트 차감할 때 포인트가 부족한 경우 예외가 발생한다.")
        @Test
        void useWithInsufficientAmount() {
            // given
            Point point = fixtureMonkey.giveMeBuilder(Point.class)
                    .set("amount", 5000)
                    .sample();
            given(pointRepository.findByUser(any(User.class)))
                    .willReturn(Optional.of(point));

            User user = fixtureMonkey.giveMeOne(User.class);

            // when, then
            assertThatThrownBy(() -> pointService.use(user, 6000))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POINT_INSUFFICIENT);

        }
    }
}
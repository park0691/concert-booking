package io.project.concertbooking.domain.point;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    IPointRepository pointRepository;

    @InjectMocks
    PointService pointService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

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

    @DisplayName("포인트가 조회되지 않는 경우 예외가 발생한다.")
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
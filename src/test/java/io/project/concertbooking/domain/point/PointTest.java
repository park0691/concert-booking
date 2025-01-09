package io.project.concertbooking.domain.point;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("포인트를 충전한다.")
    @Test
    void charge() {
        // given
        Point point = fixtureMonkey.giveMeBuilder(Point.class)
                .set("amount", 5000)
                .sample();

        // when
        point.charge(340000);

        // then
        assertThat(point.getAmount()).isEqualTo(345000);
    }

    @DisplayName("포인트가 부족한 경우 예외가 발생한다.")
    @Test
    void useWithInsufficientAmount() {
        // given
        Point point = fixtureMonkey.giveMeBuilder(Point.class)
                .set("amount", 5000)
                .sample();

        // when, then
        assertThatThrownBy(() -> point.use(6000))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POINT_INSUFFICIENT);
    }

    @DisplayName("포인트를 사용한다.")
    @Test
    void use() {
        // given
        Point point = fixtureMonkey.giveMeBuilder(Point.class)
                .set("amount", 5000)
                .sample();

        // when
        point.use(4000);

        // then
        assertThat(point.getAmount()).isEqualTo(1000);
    }
}
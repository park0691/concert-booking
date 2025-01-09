package io.project.concertbooking.domain.point;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
}
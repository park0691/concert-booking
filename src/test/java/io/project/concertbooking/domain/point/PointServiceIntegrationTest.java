package io.project.concertbooking.domain.point;

import io.project.concertbooking.support.IntegrationTestSupport;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.infrastructure.point.repository.PointHistoryJpaRepository;
import io.project.concertbooking.infrastructure.point.repository.PointJpaRepository;
import io.project.concertbooking.infrastructure.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    PointService pointService;

    @Autowired
    PointJpaRepository pointJpaRepository;

    @Autowired
    PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    IPointRepository pointRepository;

    @BeforeEach
    void beforeEach() {
        userJpaRepository.deleteAllInBatch();
        pointJpaRepository.deleteAllInBatch();
        pointHistoryJpaRepository.deleteAllInBatch();
    }

    @DisplayName("충전 시 기저장된 포인트가 조회되지 않을 때 충전 요청한 포인트만큼 포인트가 생성된다.")
    @Test
    void chargeWhenPointNotExists() {
        // given
        User user = fixtureMonkey.giveMeBuilder(User.class)
                .setNull("point")
                .sample();
        userJpaRepository.save(user);
        int chargePoint = 20000;

        // when
        pointService.charge(user, chargePoint);

        // then
        Optional<Point> pointOpt = pointRepository.findByUser(user);
        assertThat(pointOpt.isPresent()).isTrue();

        Point foundPoint = pointOpt.get();
        assertThat(foundPoint.getAmount()).isEqualTo(chargePoint);
    }

    @DisplayName("충전 시 기저장된 포인트가 조회될 때 기존 포인트에 충전 요청한 포인트만큼 더한 포인트가 저장된다.")
    @Test
    void chargeWhenPointExists() {
        // given
        User user = fixtureMonkey.giveMeBuilder(User.class)
                .setNull("point")
                .sample();
        userJpaRepository.save(user);

        Point point = Point.createPoint(user, 251320);
        pointJpaRepository.save(point);
        int chargePoint = 20120;

        // when
        pointService.charge(user, chargePoint);

        // then
        Optional<Point> pointOpt = pointRepository.findByUser(user);
        assertThat(pointOpt.isPresent()).isTrue();

        Point foundPoint = pointOpt.get();
        assertThat(foundPoint.getAmount()).isEqualTo(271440);
    }

    @DisplayName("포인트가 조회될 때 포인트 히스토리가 저장된다.")
    @Test
    void chargeWithSaveHistory() {
        // given
        User user = fixtureMonkey.giveMeBuilder(User.class)
                .setNull("point")
                .sample();
        userJpaRepository.save(user);

        Point point = Point.createPoint(user, 251320);
        pointJpaRepository.save(point);
        int chargePoint = 20120;

        PointHistory beforeHistory = pointHistoryJpaRepository.findTop1ByOrderByPointHistoryIdDesc();

        // when
        pointService.charge(user, chargePoint);

        // then
        PointHistory afterHistory = pointHistoryJpaRepository.findTop1ByOrderByPointHistoryIdDesc();
        assertThat(beforeHistory).isNull();
        assertThat(afterHistory).isNotNull();
    }
}
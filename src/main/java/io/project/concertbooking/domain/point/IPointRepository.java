package io.project.concertbooking.domain.point;

import io.project.concertbooking.domain.user.User;

import java.util.Optional;

public interface IPointRepository {
    Optional<Point> findByUser(User user);

    Optional<Point> findByUserWithLock(User user);

    Point savePoint(Point point);

    PointHistory saveHistory(PointHistory pointHistory);
}

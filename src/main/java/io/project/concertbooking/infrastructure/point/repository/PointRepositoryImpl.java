package io.project.concertbooking.infrastructure.point.repository;

import io.project.concertbooking.domain.point.IPointRepository;
import io.project.concertbooking.domain.point.Point;
import io.project.concertbooking.domain.point.PointHistory;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements IPointRepository {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Optional<Point> findByUser(User user) {
        return pointJpaRepository.findByUser(user);
    }

    @Override
    public Optional<Point> findByUserWithLock(User user) {
        return pointJpaRepository.findByUserWithPessimisticLock(user);
    }

    @Override
    public Point savePoint(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public PointHistory saveHistory(PointHistory pointHistory) {
        return pointHistoryJpaRepository.save(pointHistory);
    }
}

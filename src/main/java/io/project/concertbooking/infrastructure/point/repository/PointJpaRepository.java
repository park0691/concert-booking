package io.project.concertbooking.infrastructure.point.repository;

import io.project.concertbooking.domain.point.Point;
import io.project.concertbooking.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);
}

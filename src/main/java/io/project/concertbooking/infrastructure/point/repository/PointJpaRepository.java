package io.project.concertbooking.infrastructure.point.repository;

import io.project.concertbooking.domain.point.Point;
import io.project.concertbooking.domain.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUser(User user);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Point p WHERE p.user = :user")
    Optional<Point> findByUserWithPessimisticLock(@Param("user") User user);
}

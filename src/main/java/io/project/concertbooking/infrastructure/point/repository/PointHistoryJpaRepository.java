package io.project.concertbooking.infrastructure.point.repository;

import io.project.concertbooking.domain.point.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
    PointHistory findTop1ByOrderByPointHistoryIdDesc();
}

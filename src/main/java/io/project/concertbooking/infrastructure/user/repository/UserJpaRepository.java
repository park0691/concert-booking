package io.project.concertbooking.infrastructure.user.repository;

import io.project.concertbooking.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}

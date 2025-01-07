package io.project.concertbooking.domain.user;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(Long id);
}

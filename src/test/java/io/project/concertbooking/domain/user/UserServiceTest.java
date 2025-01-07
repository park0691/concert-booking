package io.project.concertbooking.domain.user;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    IUserRepository userRepository;

    @InjectMocks
    UserService userService;

    @DisplayName("존재하지 않는 유저 아이디로 조회하면 예외가 발생한다.")
    @Test
    void findByIdWithInvalidUserId() {
        // given
        Long userId = 1L;

        // when, then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }

    @DisplayName("존재하는 유저 아이디로 유저를 조회한다.")
    @Test
    void findById() {
        // given
        User user = User.createUser();
        given(userRepository.findById(any(Long.class)))
                .willReturn(Optional.of(user));

        // when
        User foundUser = userService.findById(1L);

        // then
        assertThat(foundUser).usingRecursiveComparison()
                .isEqualTo(user);
    }
}
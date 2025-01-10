package io.project.concertbooking.domain.concert;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    IConcertRepository concertRepository;

    @InjectMocks
    ConcertService concertService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("존재하지 않는 콘서트 아이디로 조회하면 예외가 발생한다.")
    @Test
    void findByIdWithInvalidConcertId() {
        // given
        Long concertId = 1L;

        // when, then
        assertThatThrownBy(() -> concertService.findById(concertId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONCERT_NOT_FOUND);
    }

    @DisplayName("존재하는 콘서트 아이디로 콘서트를 조회한다.")
    @Test
    void findById() {
        // given
        Concert concert = fixtureMonkey.giveMeOne(Concert.class);
        given(concertRepository.findById(any(Long.class)))
                .willReturn(Optional.of(concert));

        // when
        Concert foundConcert = concertService.findById(1L);

        // then
        assertThat(foundConcert).usingRecursiveComparison()
                .isEqualTo(concert);
    }

    @DisplayName("존재하지 않는 스케줄 아이디로 콘서트 스케줄을 조회하면 예외가 발생한다.")
    @Test
    void findScheduleByIdWithInvalidScheduleId() {
        // given
        Long scheduleId = 1L;

        // when, then
        assertThatThrownBy(() -> concertService.findScheduleById(scheduleId))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SCHEDULE_NOT_FOUND);
    }

    @DisplayName("존재하는 스케줄 아이디로 콘서트 스케줄을 조회한다.")
    @Test
    void findScheduleById() {
        // given
        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);
        given(concertRepository.findScheduleById(any(Long.class)))
                .willReturn(Optional.of(concertSchedule));

        // when
        ConcertSchedule foundSchedule = concertService.findScheduleById(1L);

        // then
        assertThat(foundSchedule).usingRecursiveComparison()
                .isEqualTo(concertSchedule);
    }
}
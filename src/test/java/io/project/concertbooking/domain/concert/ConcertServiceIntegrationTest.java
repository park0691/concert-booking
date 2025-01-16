package io.project.concertbooking.domain.concert;

import com.navercorp.fixturemonkey.customizer.Values;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import io.project.concertbooking.support.IntegrationTestSupport;
import net.jqwik.api.Arbitraries;
import net.jqwik.time.api.DateTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[ConcertService - 통합 테스트]")
class ConcertServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    ConcertService concertService;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    IConcertRepository concertRepository;

    @Nested
    @DisplayName("[findSchedulesBy() - 콘서트 스케줄 조회 페이지네이션 테스트]")
    class FindSchedulesBy {

        @DisplayName("콘서트 ID 조회시 첫 번째 페이지, 마지막 페이지의 페이지네이션이 정상 동작한다.")
        @Test
        void findSchedulesBy() {
            // given
            int totalCount = 33;
            LocalDateTime now = LocalDateTime.now();
            Concert concert = concertJpaRepository.save(
                    fixtureMonkey.giveMeBuilder(Concert.class)
                            .set("name", Arbitraries.strings().withCharRange('a', 'z').ofLength(10))
                            .sample()
            );
            concertScheduleJpaRepository.saveAll(
                    fixtureMonkey.giveMeBuilder(ConcertSchedule.class)
                            .set("concert", Values.just(concert))
                            .set("scheduleDt", DateTimes.dateTimes().atTheEarliest(now.plusDays(7L))
                                    .atTheLatest(now.plusDays(60L)))
                            .sampleList(totalCount)
            );

            // when
            PageRequest pageRequest = PageRequest.of(0, 5);
            Page<ConcertSchedule> schedules = concertService.findSchedulesBy(concert, pageRequest);

            PageRequest pageRequest2 = PageRequest.of(6, 5);
            Page<ConcertSchedule> schedules2 = concertService.findSchedulesBy(concert, pageRequest2);

            // then
            assertThat(schedules).isNotNull();
            assertThat(schedules.getTotalPages()).isEqualTo(7);
            assertThat(schedules.getSize()).isEqualTo(5);
            assertThat(schedules.getNumber()).isEqualTo(0);
            assertThat(schedules.getNumberOfElements()).isEqualTo(5);
            assertThat(schedules.getTotalElements()).isEqualTo(totalCount);

            assertThat(schedules2).isNotNull();
            assertThat(schedules2.getTotalPages()).isEqualTo(7);
            assertThat(schedules2.getSize()).isEqualTo(5);
            assertThat(schedules2.getNumber()).isEqualTo(6);
            assertThat(schedules2.getNumberOfElements()).isEqualTo(3);
            assertThat(schedules2.getTotalElements()).isEqualTo(totalCount);
        }
    }
}
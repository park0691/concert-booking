package io.project.concertbooking.domain.concert;

import io.project.concertbooking.support.IntegrationTestSupport;
import io.project.concertbooking.infrastructure.concert.repository.ConcertJpaRepository;
import io.project.concertbooking.infrastructure.concert.repository.ConcertScheduleJpaRepository;
import net.jqwik.api.Arbitraries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConcertServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    ConcertService concertService;

    @Autowired
    ConcertJpaRepository concertJpaRepository;

    @Autowired
    ConcertScheduleJpaRepository concertScheduleJpaRepository;

    @Autowired
    IConcertRepository concertRepository;

    @BeforeEach
    void beforeEach() {
        concertJpaRepository.deleteAllInBatch();
        concertScheduleJpaRepository.deleteAllInBatch();
    }

    @DisplayName("콘서트 ID 조회시 첫 번째 페이지, 마지막 페이지의 페이지네이션이 정상 동작한다.")
    @Test
    public void findScheduleByConcert() {
        // given
        int totalCount = 33;
        Concert concert = fixtureMonkey.giveMeBuilder(Concert.class)
                .set("name", Arbitraries.strings().withCharRange('a', 'z').ofLength(10))
                .sample();

        concertJpaRepository.save(concert);

        for (int i = 0; i < totalCount; i++) {
            concertScheduleJpaRepository.save(
                    ConcertSchedule.builder()
                            .concert(concert)
                            .scheduleDt(LocalDateTime.now())
                            .build()
            );
        }

        // when
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<ConcertSchedule> schedules = concertService.findScheduleByConcert(concert, pageRequest);

        PageRequest pageRequest2 = PageRequest.of(6, 5);
        Page<ConcertSchedule> schedules2 = concertService.findScheduleByConcert(concert, pageRequest2);

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
package io.project.concertbooking.domain.seat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    ISeatRepository seatRepository;

    @InjectMocks
    SeatService seatService;

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @DisplayName("콘서트 스케줄을 통해 예약 가능한(빈) 좌석만 조회한다.")
    @Test
    void findAvailableSeats() {
        // given
        Seat seat1 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.OCCUPIED)
                .sample();
        Seat seat2 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.RESERVED)
                .sample();
        Seat seat3 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();
        Seat seat4 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.OCCUPIED)
                .sample();
        Seat seat5 = fixtureMonkey.giveMeBuilder(Seat.class)
                .set("status", SeatStatus.EMPTY)
                .sample();

        given(seatRepository.findAllByConcertSchedule(any(ConcertSchedule.class)))
                .willReturn(List.of(seat1, seat2, seat3, seat4, seat5));

        ConcertSchedule concertSchedule = fixtureMonkey.giveMeOne(ConcertSchedule.class);

        // when
        List<Seat> seats = seatService.findAvailableSeats(concertSchedule);

        // then
        assertThat(seats).isNotEmpty();
        assertThat(seats).hasSize(2);
        assertThat(seats).containsExactly(seat3, seat5);
    }
}
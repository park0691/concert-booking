package io.project.concertbooking.infrastructure.seat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.project.concertbooking.domain.seat.Seat;
import io.project.concertbooking.domain.seat.enums.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.project.concertbooking.domain.seat.QSeat.seat;

@Repository
@RequiredArgsConstructor
public class SeatQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Long updateSeatStatusByIdsIn(SeatStatus condStat, List<Seat> condSeats) {
        return queryFactory.update(seat)
                .set(seat.status, condStat)
                .where(seat.in(condSeats))
                .execute();
    }
}

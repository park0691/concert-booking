package io.project.concertbooking.infrastructure.seat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.project.concertbooking.domain.seat.SeatReservation;
import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.project.concertbooking.domain.seat.QSeatReservation.seatReservation;

@Repository
@RequiredArgsConstructor
public class SeatReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Long updateReservationStatusIn(SeatReservationStatus condStat, List<SeatReservation> condSeatReservationss) {
        return queryFactory.update(seatReservation)
                .set(seatReservation.status, condStat)
                .where(seatReservation.in(condSeatReservationss))
                .execute();
    }
}

package io.project.concertbooking.infrastructure.reservation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.project.concertbooking.domain.reservation.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Long updateReservationStatusIn(ReservationStatus condStat, List<Reservation> condReservations) {
        return queryFactory.update(reservation)
                .set(reservation.status, condStat)
                .where(reservation.in(condReservations))
                .execute();
    }
}

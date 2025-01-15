package io.project.concertbooking.infrastructure.queue.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.enums.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static io.project.concertbooking.domain.queue.QQueue.queue;

@Repository
@RequiredArgsConstructor
public class QueueQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Long updateStatusByExpDtLt(QueueStatus condStat, LocalDateTime condDt) {
        return queryFactory.update(queue)
                .set(queue.status, condStat)
                .where(queue.expDt.lt(condDt))
                .execute();
    }

    public Long findCountByStatus(QueueStatus condStat) {
        return queryFactory.select(queue.count())
                .from(queue)
                .where(queue.status.eq(condStat))
                .fetchOne();
    }

    public List<Queue> findAllByStatusOrderByIdAscLimit(QueueStatus condStat, Integer condLimit) {
        return queryFactory.selectFrom(queue)
                .where(queue.status.eq(condStat))
                .orderBy(queue.queueId.asc())
                .limit(condLimit)
                .fetch();
    }

    public Long updateStatusByIds(QueueStatus condStat, List<Long> condIds) {
        return queryFactory.update(queue)
                .set(queue.status, condStat)
                .where(queue.queueId.in(condIds))
                .execute();
    }
}

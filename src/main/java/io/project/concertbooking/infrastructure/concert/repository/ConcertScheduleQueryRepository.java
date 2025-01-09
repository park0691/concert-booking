package io.project.concertbooking.infrastructure.concert.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.project.concertbooking.domain.concert.Concert;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.project.concertbooking.domain.concert.QConcert.concert;
import static io.project.concertbooking.domain.concert.QConcertSchedule.concertSchedule;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<ConcertSchedule> findAllByConcert(Concert condConcert, Pageable pageable) {
        List<ConcertSchedule> contents = queryFactory.selectFrom(concertSchedule)
                .where(concert.eq(condConcert))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(concertSchedule.count())
                .from(concertSchedule)
                .where(concert.eq(condConcert));

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }
}

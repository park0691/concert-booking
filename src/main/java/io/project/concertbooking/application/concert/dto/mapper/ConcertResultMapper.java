package io.project.concertbooking.application.concert.dto.mapper;

import io.project.concertbooking.application.concert.dto.ScheduleResult;
import io.project.concertbooking.domain.concert.ConcertSchedule;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ConcertResultMapper {

    @Mapping(target = "scheduleId", source = "concertScheduleId")
    ScheduleResult.Data toScheduleDataOfResult(ConcertSchedule schedule);

    @Mapping(target = "page", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "dataCount", source = "numberOfElements")
    @Mapping(target = "dataTotalCount", source = "totalElements")
    @Mapping(target = "pageTotalCount", source = "totalPages")
    ScheduleResult.Page toSchedulePageOfResult(Page<ConcertSchedule> schedules);

    @AfterMapping
    default void decrementPage(@MappingTarget ScheduleResult.Page page) {
        page.setPage(page.getPage() + 1);
    }
}

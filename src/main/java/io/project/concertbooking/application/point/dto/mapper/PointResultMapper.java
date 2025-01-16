package io.project.concertbooking.application.point.dto.mapper;

import io.project.concertbooking.application.point.dto.PointResult;
import io.project.concertbooking.domain.point.Point;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointResultMapper {

    PointResult toPointResult(Point point);
}

package io.project.concertbooking.interfaces.api.v1.point.response.mapper;

import io.project.concertbooking.application.point.dto.PointResult;
import io.project.concertbooking.interfaces.api.v1.point.response.PointResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointResponseMapper {

    @Mapping(target = "currentAmount", source = "amount")
    PointResponse toPointResponse(PointResult pointResult);
}

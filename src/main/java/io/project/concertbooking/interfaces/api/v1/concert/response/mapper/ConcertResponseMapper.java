package io.project.concertbooking.interfaces.api.v1.concert.response.mapper;

import io.project.concertbooking.application.reservation.dto.ReservationResult;
import io.project.concertbooking.interfaces.api.v1.concert.response.ReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConcertResponseMapper {

    @Mapping(target = "scheduleDt", source = "concertScheduleDt")
    @Mapping(target = "expirationDt", source = "expDt")
    ReservationResponse.Info toReservationInfoOfResponse(ReservationResult reservationResult);
}

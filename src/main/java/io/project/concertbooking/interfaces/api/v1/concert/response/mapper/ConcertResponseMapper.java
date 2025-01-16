package io.project.concertbooking.interfaces.api.v1.concert.response.mapper;

import io.project.concertbooking.application.concert.dto.ScheduleResult;
import io.project.concertbooking.application.concert.dto.SeatResult;
import io.project.concertbooking.application.reservation.dto.ReservationResult;
import io.project.concertbooking.interfaces.api.v1.concert.response.ReservationResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.ScheduleResponse;
import io.project.concertbooking.interfaces.api.v1.concert.response.SeatResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConcertResponseMapper {

    @Mapping(target = "scheduleDt", source = "concertScheduleDt")
    @Mapping(target = "expirationDt", source = "expDt")
    ReservationResponse.Info toReservationInfoOfResponse(ReservationResult reservationResult);

    ScheduleResponse.Info toScheduleInfoOfResponse(ScheduleResult.Data scheduleDataOfResult);

    ScheduleResponse.Page toSchedulePageOfResponse(ScheduleResult.Page schedulePageOfResult);

    SeatResponse.Info toSeatInfoOfResponse(SeatResult seatResult);
}

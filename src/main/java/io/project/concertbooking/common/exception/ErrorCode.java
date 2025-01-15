package io.project.concertbooking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* User */
    USER_NOT_FOUND(NOT_FOUND, "US001", "해당 유저의 정보를 찾을 수 없습니다."),

    /* Concert */
    SCHEDULE_NOT_FOUND(NOT_FOUND, "CN001", "콘서트 스케줄을 찾을 수 없습니다."),
    CONCERT_NOT_FOUND(NOT_FOUND, "CN002", "콘서트 정보를 찾을 수 없습니다."),
    SEAT_NOT_FOUND(NOT_FOUND, "CN003", "좌석 정보를 찾을 수 없습니다."),
    SEAT_NOT_AVAILABLE(BAD_REQUEST, "CN004", "이미 예약 또는 확정된 좌석이 존재합니다."),

    /* Reservation */
    RESERVATION_NOT_FOUND(NOT_FOUND, "RE001", "좌석 예약을 찾을 수 없습니다."),

    /* Queue */
    TOKEN_NOT_FOUND(NOT_FOUND, "QE001", "발급되지 않은 대기열 토큰입니다."),
    TOKEN_NOT_ACTIVE(NOT_FOUND, "QE002", "활성화 상태가 아닌 토큰입니다."),

    /* Payment */

    /* Point */
    POINT_NOT_FOUND(NOT_FOUND, "PT001", "유저의 포인트 정보를 찾을 수 없습니다."),
    POINT_INSUFFICIENT(BAD_REQUEST, "PT002", "포인트가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

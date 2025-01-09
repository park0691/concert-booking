package io.project.concertbooking.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */

    /* 401 UNAUTHORIZED : 인증되지 않은 요청 */

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "해당 유저의 정보를 찾을 수 없습니다."),
    POINT_NOT_FOUND(NOT_FOUND, "유저의 포인트 정보를 찾을 수 없습니다."),
    CONCERT_NOT_FOUND(NOT_FOUND, "콘서트 정보를 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(NOT_FOUND, "발급되지 않은 대기열 토큰입니다.");

    /* 409 CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    private final HttpStatus httpStatus;
    private final String message;
}

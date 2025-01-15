package io.project.concertbooking.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String path;
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public ErrorResponse(String path, int status, String error, String code, String message) {
        this.path = path;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(HttpServletRequest request, ErrorCode errorCode) {
        return new ErrorResponse(request.getRequestURI(), errorCode.getHttpStatus().value(), errorCode.getHttpStatus().name(),
                errorCode.getCode(), errorCode.getMessage());
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(HttpServletRequest request, ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(request, errorCode));
    }
}

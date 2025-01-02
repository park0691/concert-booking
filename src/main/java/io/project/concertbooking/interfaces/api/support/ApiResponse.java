package io.project.concertbooking.interfaces.api.support;

import lombok.Getter;

@Getter
public class ApiResponse<T> extends ApiBaseResponse {

    private T data;

    public ApiResponse(int code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(200, "Success", data);
    }
}

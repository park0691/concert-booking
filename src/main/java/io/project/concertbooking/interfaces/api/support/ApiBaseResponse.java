package io.project.concertbooking.interfaces.api.support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiBaseResponse {

    private int code;
    private String message;

    public static ApiBaseResponse ok() {
        return new ApiBaseResponse(HttpStatus.OK.value(), "Success");
    }
}

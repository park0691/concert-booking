package io.project.concertbooking.interfaces.api.v1.user;

import io.project.concertbooking.interfaces.api.support.ApiBaseResponse;
import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.user.request.ChargeCashRequest;
import io.project.concertbooking.interfaces.api.v1.user.request.GetCashRequest;
import io.project.concertbooking.interfaces.api.v1.user.response.GetCashResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping("/cash/charge")
    public ApiBaseResponse chargeCash(@RequestBody ChargeCashRequest chargeCashRequest) {
        return ApiBaseResponse.ok();
    }

    @GetMapping("/cash")
    public ApiResponse<?> getCash(@RequestBody GetCashRequest getCashRequest) {
        return ApiResponse.ok(GetCashResponse.builder()
                .cash(10000)
                .build());
    }
}

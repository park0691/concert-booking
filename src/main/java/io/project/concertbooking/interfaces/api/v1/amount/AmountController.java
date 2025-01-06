package io.project.concertbooking.interfaces.api.v1.amount;

import io.project.concertbooking.interfaces.api.support.ApiResponse;
import io.project.concertbooking.interfaces.api.v1.amount.request.ChargeAmountRequest;
import io.project.concertbooking.interfaces.api.v1.amount.request.GetAmountRequest;
import io.project.concertbooking.interfaces.api.v1.amount.response.ChargeAmountResponse;
import io.project.concertbooking.interfaces.api.v1.amount.response.GetAmountResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amounts")
public class AmountController {

    @PostMapping("/charge")
    public ApiResponse<?> chargeAmount(@RequestBody ChargeAmountRequest chargeAmountRequest) {
        return ApiResponse.ok(ChargeAmountResponse.builder()
                .currentAmount(12345)
                .build());
    }

    @GetMapping
    public ApiResponse<?> getAmount(@RequestBody GetAmountRequest getAmountRequest) {
        return ApiResponse.ok(GetAmountResponse.builder()
                .amount(10000)
                .build());
    }
}
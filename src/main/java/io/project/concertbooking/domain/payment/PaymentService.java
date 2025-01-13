package io.project.concertbooking.domain.payment;

import io.project.concertbooking.domain.payment.enums.PaymentMethod;
import io.project.concertbooking.domain.payment.enums.PaymentStatus;
import io.project.concertbooking.domain.seat.SeatReservation;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IPaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(User user, SeatReservation seatReservation, Integer price, PaymentMethod method) {
        return paymentRepository.save(
                Payment.createPayment(user, seatReservation, price, method, PaymentStatus.SUCCESS)
        );
    }
}

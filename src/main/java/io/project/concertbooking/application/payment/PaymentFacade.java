package io.project.concertbooking.application.payment;

import io.project.concertbooking.application.payment.dto.PaymentResult;
import io.project.concertbooking.domain.payment.Payment;
import io.project.concertbooking.domain.payment.PaymentService;
import io.project.concertbooking.domain.payment.enums.PaymentMethod;
import io.project.concertbooking.domain.point.PointService;
import io.project.concertbooking.domain.queue.Queue;
import io.project.concertbooking.domain.queue.QueueService;
import io.project.concertbooking.domain.reservation.Reservation;
import io.project.concertbooking.domain.reservation.ReservationService;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final PointService pointService;
    private final QueueService queueService;

    @Transactional
    public PaymentResult processPayments(String token, Long userId, Long reservationId, Integer price) {
        User user = userService.findById(userId);
        Reservation reservation = reservationService.findReservation(reservationId);

        // 포인트를 차감한다.
        pointService.use(user, price);

        // 결제 내역을 생성한다.
        Payment payment = paymentService.createPayment(user, reservation, price, PaymentMethod.POINT);

        // 예약을 확정 처리한다.
        reservationService.finalizeReservation(reservation);

        // 대기열 만료 처리한다.
        Queue queue = queueService.findByToken(token);
        queueService.expire(queue);

        return PaymentResult.of(payment, reservation);
    }
}

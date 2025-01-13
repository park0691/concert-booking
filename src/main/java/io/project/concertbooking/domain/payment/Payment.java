package io.project.concertbooking.domain.payment;

import io.project.concertbooking.domain.payment.enums.converter.PaymentMethodConverter;
import io.project.concertbooking.domain.payment.enums.converter.PaymentStatusConverter;
import io.project.concertbooking.domain.payment.enums.PaymentMethod;
import io.project.concertbooking.domain.payment.enums.PaymentStatus;
import io.project.concertbooking.domain.seat.SeatReservation;
import io.project.concertbooking.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID", updatable = false)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAT_RESERVATION_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SeatReservation seatReservation;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "PAYMENT_METHOD")
    @Convert(converter = PaymentMethodConverter.class)
    private PaymentMethod method;

    @Column(name = "STATUS")
    @Convert(converter = PaymentStatusConverter.class)
    private PaymentStatus status;

    @CreatedDate
    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Builder
    private Payment(User user, SeatReservation seatReservation, Integer price, PaymentMethod method, PaymentStatus status, LocalDateTime regDt) {
        this.user = user;
        this.seatReservation = seatReservation;
        this.price = price;
        this.method = method;
        this.status = status;
        this.regDt = regDt;
    }

    public static Payment createPayment(User user, SeatReservation reservation, Integer price, PaymentMethod method, PaymentStatus status) {
        return Payment.builder()
                .user(user)
                .seatReservation(reservation)
                .price(price)
                .method(method)
                .status(status)
                .build();
    }
}

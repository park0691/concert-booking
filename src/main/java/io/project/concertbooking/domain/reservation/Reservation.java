package io.project.concertbooking.domain.reservation;

import io.project.concertbooking.domain.concert.Seat;
import io.project.concertbooking.domain.reservation.enums.ReservationStatus;
import io.project.concertbooking.domain.reservation.enums.converter.ReservationStatusConverter;
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
@Table(name = "RESERVATION")
@NoArgsConstructor
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID", updatable = false)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "SEAT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Seat seat;

    @Column(name = "SEAT_NUMBER")
    private Integer seatNumber;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "STATUS")
    @Convert(converter = ReservationStatusConverter.class)
    private ReservationStatus status;

    @CreatedDate
    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Builder
    private Reservation(User user, Seat seat, Integer seatNumber, Integer price, ReservationStatus status, LocalDateTime regDt) {
        this.user = user;
        this.seat = seat;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.regDt = regDt;
    }

    public static Reservation createReservation(User user, Seat seat, Integer seatNumber, Integer price, ReservationStatus status) {
        return Reservation.builder()
                .user(user)
                .seat(seat)
                .seatNumber(seatNumber)
                .price(price)
                .status(status)
                .build();
    }

    public void confirm() {
        this.status = ReservationStatus.PAID;
        seat.occupy();
    }
}

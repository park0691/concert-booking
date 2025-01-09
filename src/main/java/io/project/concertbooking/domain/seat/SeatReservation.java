package io.project.concertbooking.domain.seat;

import io.project.concertbooking.domain.seat.enums.SeatReservationStatus;
import io.project.concertbooking.domain.seat.enums.converter.SeatReservationStatusConverter;
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
@Table(name = "SEAT_RESERVATION")
@NoArgsConstructor
@Getter
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_RESERVATION_ID", updatable = false)
    private Long seatReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEAT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Seat seat;

    @Column(name = "SEAT_NUMBER")
    private Integer seatNumber;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "STATUS")
    @Convert(converter = SeatReservationStatusConverter.class)
    private SeatReservationStatus status;

    @CreatedDate
    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Builder
    private SeatReservation(User user, Seat seat, Integer seatNumber, Integer price, SeatReservationStatus status, LocalDateTime regDt) {
        this.user = user;
        this.seat = seat;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
        this.regDt = regDt;
    }

    public static SeatReservation createSeatReservation(User user, Seat seat, Integer seatNumber, Integer price, SeatReservationStatus status) {
        return SeatReservation.builder()
                .user(user)
                .seat(seat)
                .seatNumber(seatNumber)
                .price(price)
                .status(status)
                .build();
    }
}

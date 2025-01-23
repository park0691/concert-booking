package io.project.concertbooking.domain.concert;

import io.project.concertbooking.domain.concert.enums.SeatStatus;
import io.project.concertbooking.domain.concert.enums.converter.SeatStatusConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SEAT")
@NoArgsConstructor
@Getter
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID", updatable = false)
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONCERT_SCHEDULE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ConcertSchedule concertSchedule;

    @Column(name = "SEAT_NUMBER")
    private Integer number;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "STATUS")
    @Convert(converter = SeatStatusConverter.class)
    private SeatStatus status;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Builder
    private Seat(ConcertSchedule concertSchedule, Integer number, Integer price, SeatStatus status) {
        this.concertSchedule = concertSchedule;
        this.number = number;
        this.price = price;
        this.status = status;
    }

    public boolean isReserved() {
        return this.status == SeatStatus.RESERVED;
    }

    public boolean isOccupied() {
        return this.status == SeatStatus.OCCUPIED;
    }

    public boolean isEmpty() {
        return this.status == SeatStatus.EMPTY;
    }

    public void reserve() {
        this.status = SeatStatus.RESERVED;
    }

    public void occupy() {
        this.status = SeatStatus.OCCUPIED;
    }
}

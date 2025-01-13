package io.project.concertbooking.domain.concert;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONCERT_SCHEDULE")
@NoArgsConstructor
@Getter
public class ConcertSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONCERT_SCHEDULE_ID", updatable = false)
    private Long concertScheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONCERT_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Concert concert;

    @Column(name = "SCHEDULE_DT")
    private LocalDateTime scheduleDt;

    @Builder
    private ConcertSchedule(Concert concert, LocalDateTime scheduleDt) {
        this.scheduleDt = scheduleDt;
    }
}

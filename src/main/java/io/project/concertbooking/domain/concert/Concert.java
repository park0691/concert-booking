package io.project.concertbooking.domain.concert;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CONCERT")
@NoArgsConstructor
@Getter
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONCERT_ID", updatable = false)
    private Long concertId;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "concert")
    private List<ConcertSchedule> concertSchedules = new ArrayList<>();

    @Builder
    private Concert(Long concertId, String name, List<ConcertSchedule> concertSchedules) {
        this.concertId = concertId;
        this.name = name;
        this.concertSchedules = concertSchedules;
    }
}

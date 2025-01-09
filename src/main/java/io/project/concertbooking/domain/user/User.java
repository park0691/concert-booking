package io.project.concertbooking.domain.user;

import io.project.concertbooking.domain.point.Point;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", updatable = false)
    private Long userId;

    @OneToOne(mappedBy = "user")
    private Point point;

    @Builder
    private User(Point point) {
        this.point = point;
    }
}

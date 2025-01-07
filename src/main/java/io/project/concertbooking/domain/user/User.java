package io.project.concertbooking.domain.user;

import io.project.concertbooking.domain.point.UserPoint;
import jakarta.persistence.*;
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
    private UserPoint amount;
}

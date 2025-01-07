package io.project.concertbooking.domain.point;

import io.project.concertbooking.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_POINT")
@NoArgsConstructor
@Getter
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_POINT_ID", updatable = false)
    private Long userPointId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Builder
    private UserPoint(Long userPointId, User user, Integer amount) {
        this.userPointId = userPointId;
        this.user = user;
        this.amount = amount;
    }
}

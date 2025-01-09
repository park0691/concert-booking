package io.project.concertbooking.domain.point;

import io.project.concertbooking.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "POINT")
@NoArgsConstructor
@Getter
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POINT_ID", updatable = false)
    private Long pointId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Builder
    private Point(User user, Integer amount) {
        this.user = user;
        this.amount = amount;
    }

    public static Point createPoint(User user, Integer amount) {
        return Point.builder()
                .user(user)
                .amount(amount)
                .build();
    }

    public void charge(Integer point) {
        this.amount += point;
    }
}

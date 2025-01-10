package io.project.concertbooking.domain.point;

import io.project.concertbooking.domain.point.enums.TransactionType;
import io.project.concertbooking.domain.point.enums.converter.TransactionTypeConverter;
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
@Table(name = "POINT_HISTORY")
@NoArgsConstructor
@Getter
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POINT_HISTORY_ID", updatable = false)
    private Long pointHistoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "TRANSACTION_TYPE")
    @Convert(converter = TransactionTypeConverter.class)
    private TransactionType type;

    @CreatedDate
    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Builder
    private PointHistory(User user, Integer amount, TransactionType type, LocalDateTime regDt) {
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.regDt = regDt;
    }

    public static PointHistory createPointHistory(User user, Integer amount, TransactionType type) {
        return PointHistory.builder()
                .user(user)
                .amount(amount)
                .type(type)
                .build();
    }
}

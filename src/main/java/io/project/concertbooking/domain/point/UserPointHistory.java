package io.project.concertbooking.domain.point;

import io.project.concertbooking.domain.point.enums.TransactionType;
import io.project.concertbooking.domain.point.enums.converter.TransactionTypeConverter;
import io.project.concertbooking.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_POINT_HISTORY")
@NoArgsConstructor
@Getter
public class UserPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_POINT_HISTORY_ID", updatable = false)
    private Long userPointHistoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "TRANSACTION_TYPE")
    @Convert(converter = TransactionTypeConverter.class)
    private TransactionType type;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;
}

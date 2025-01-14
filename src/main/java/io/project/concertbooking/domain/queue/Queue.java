package io.project.concertbooking.domain.queue;

import io.project.concertbooking.domain.queue.enums.QueueStatus;
import io.project.concertbooking.domain.queue.enums.converter.QueueStatusConverter;
import io.project.concertbooking.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "QUEUE")
@NoArgsConstructor
@Getter
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUEUE_ID", updatable = false)
    private Long queueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "STATUS")
    @Convert(converter = QueueStatusConverter.class)
    private QueueStatus status;

    @CreatedDate
    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    @Column(name = "EXP_DT")
    private LocalDateTime expDt;

    @Builder
    private Queue(User user, String token, QueueStatus status, LocalDateTime regDt, LocalDateTime modDt, LocalDateTime expDt) {
        this.user = user;
        this.token = token;
        this.status = status;
        this.regDt = regDt;
        this.modDt = modDt;
        this.expDt = expDt;
    }

    public static Queue create(User user, String token, LocalDateTime expDt) {
        return Queue.builder()
                .user(user)
                .token(token)
                .status(QueueStatus.WAITING)
                .expDt(expDt)
                .build();
    }

    public boolean isExpired() {
        return this.status == QueueStatus.EXPIRED;
    }

    public boolean isWaiting() {
        return this.status == QueueStatus.WAITING;
    }

    public void expireQueueToken() {
        this.status = QueueStatus.EXPIRED;
    }
}

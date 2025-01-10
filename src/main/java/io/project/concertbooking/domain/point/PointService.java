package io.project.concertbooking.domain.point;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.point.enums.TransactionType;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PointService {

    private final IPointRepository pointRepository;

    public Point findPoint(User user) {
        return pointRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));
    }

    @Transactional
    public Point charge(User user, Integer chargePoint) {
        Optional<Point> pointOpt = pointRepository.findByUser(user);

        Point point;

        if (pointOpt.isPresent()) {
            point = pointOpt.get();
            point.charge(chargePoint);
        } else {
            point = Point.createPoint(user, chargePoint);
        }
        saveHistory(user, chargePoint, TransactionType.CHARGE);

        return pointRepository.savePoint(point);
    }

    @Transactional
    public Point use(User user, Integer usePoint) {
        Point point = pointRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        point.use(usePoint);
        saveHistory(user, usePoint, TransactionType.USE);
        return point;
    }

    private void saveHistory(User user, Integer point, TransactionType transactionType) {
        pointRepository.saveHistory(
                PointHistory.createPointHistory(
                        user,
                        point,
                        transactionType
                )
        );
    }
}

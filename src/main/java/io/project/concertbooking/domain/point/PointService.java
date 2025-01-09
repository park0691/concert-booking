package io.project.concertbooking.domain.point;

import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.domain.point.enums.TransactionType;
import io.project.concertbooking.domain.user.IUserRepository;
import io.project.concertbooking.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final IPointRepository pointRepository;

    @Transactional(readOnly = true)
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

        pointRepository.saveHistory(
                PointHistory.createPointHistory(
                        user,
                        chargePoint,
                        TransactionType.CHARGE
                )
        );

        return pointRepository.savePoint(point);
    }
}

package io.project.concertbooking.application.point;

import io.project.concertbooking.application.point.dto.PointResult;
import io.project.concertbooking.application.point.dto.mapper.PointResultMapper;
import io.project.concertbooking.domain.point.PointService;
import io.project.concertbooking.domain.user.User;
import io.project.concertbooking.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;
    private final PointResultMapper resultMapper;

    @Transactional
    public PointResult chargePoint(Long userId, Integer point) {
        User user = userService.findById(userId);
        return resultMapper.toPointResult(pointService.chargeWithDistributedSpinLock(user, point));
    }

    public PointResult getPoint(Long userId) {
        User user = userService.findById(userId);
        return resultMapper.toPointResult(pointService.findPoint(user));
    }
}

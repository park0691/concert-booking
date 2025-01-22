package io.project.concertbooking.application.aop;

import io.project.concertbooking.common.annotation.DistributedSpinLock;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.common.util.CustomSpELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(0)
@Component
@RequiredArgsConstructor
public class DistributedSpinLockAspect {

    private static final String LOCK_PREFIX = "LOCK:";

    private final LockManager lockManager;

    @Around("@annotation(io.project.concertbooking.common.annotation.DistributedSpinLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedSpinLock lock = method.getAnnotation(DistributedSpinLock.class);
        String key = LOCK_PREFIX + CustomSpELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), lock.key());
        log.info("DistributedLock started with key [%s]".formatted(key));

        long startTime = System.currentTimeMillis();
        long endTime = startTime + lock.timeUnit().toMillis(lock.waitTime());

        boolean acquired;
        while (!(acquired = lockManager.lock(key, lock.leaseTime(), lock.timeUnit()))) {
            if (System.currentTimeMillis() > endTime) {
                throw new CustomException(ErrorCode.COMMON_BAD_REQUEST);
            }
            // Spin Lock delay
            Thread.sleep(100);
        }

        try {
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(key);
        }
    }
}

package io.project.concertbooking.application.aop;

import io.project.concertbooking.common.annotation.DistributedSimpleLock;
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
public class DistributedSimpleLockAspect {

    private static final String LOCK_PREFIX = "LOCK:";

    private final LockManager lockManager;

    @Around("@annotation(io.project.concertbooking.common.annotation.DistributedSimpleLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedSimpleLock lock = method.getAnnotation(DistributedSimpleLock.class);
        String key = LOCK_PREFIX + CustomSpELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), lock.key());
        log.info("DistributedLock started with key [%s]".formatted(key));

        boolean acquired = lockManager.lock(key, lock.leaseTime(), lock.timeUnit());
        if (!acquired) {
            throw new CustomException(ErrorCode.COMMON_BAD_REQUEST);
        }

        try {
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(key);
        }
    }
}

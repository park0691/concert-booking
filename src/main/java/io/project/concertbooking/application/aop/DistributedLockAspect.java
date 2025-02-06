package io.project.concertbooking.application.aop;

import io.project.concertbooking.common.annotation.DistributedLock;
import io.project.concertbooking.common.exception.CustomException;
import io.project.concertbooking.common.exception.ErrorCode;
import io.project.concertbooking.common.util.CustomSpELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(0)
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(io.project.concertbooking.common.annotation.DistributedLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock lock = method.getAnnotation(DistributedLock.class);
        String key = LOCK_PREFIX + CustomSpELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), lock.key());
        log.info("DistributedLock started with key [{}]", key);

        RLock rLock = redissonClient.getLock(key);

        try {
            boolean acquired = rLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeUnit());
            if (!acquired) {
                throw new CustomException(ErrorCode.COMMON_BAD_REQUEST);
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new CustomException(ErrorCode.COMMON_BAD_REQUEST);
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock: methodName [{}], key [{}]",
                        method.getName(), key
                );
            }
        }
    }
}

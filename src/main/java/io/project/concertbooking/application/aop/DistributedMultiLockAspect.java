package io.project.concertbooking.application.aop;

import io.project.concertbooking.common.annotation.DistributedMultiLock;
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
import java.util.Arrays;

@Slf4j
@Aspect
@Order(0)
@Component
@RequiredArgsConstructor
public class DistributedMultiLockAspect {

    private static final String LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;

    @Around("@annotation(io.project.concertbooking.common.annotation.DistributedMultiLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedMultiLock lock = method.getAnnotation(DistributedMultiLock.class);

        String ids = String.valueOf(CustomSpELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), lock.keyIds()));
        String[] keys = Arrays.stream(ids.split(","))
                .map(s -> LOCK_PREFIX + lock.keyPrefix() + s)
                .toArray(String[]::new);
        RLock multiLock = redissonClient.getMultiLock(
                Arrays.stream(keys)
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );

        try {
            log.info("DistributedMultiLock is locking with keys {}", Arrays.toString(keys));
            boolean acquired = multiLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeUnit());
            if (!acquired) {
                throw new InterruptedException();
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            log.error("DistributedMultiLock failed to acquire lock with keys {}", Arrays.toString(keys));
            throw new CustomException(ErrorCode.COMMON_REQUEST_CONFLICT);
        } finally {
            try {
                log.info("DistributedMultiLock unlocking with keys {}", Arrays.toString(keys));
                multiLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.error("DistributedMultiLock already unlocked: methodName {}, keys {}",
                        method.getName(), Arrays.toString(keys)
                );
            }
        }
    }
}

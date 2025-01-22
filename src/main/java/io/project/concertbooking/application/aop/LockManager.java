package io.project.concertbooking.application.aop;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LockManager {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean lock(String key, Long leaseTime, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "lock", leaseTime, timeUnit));
    }

    public boolean unlock(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}

package com.shop.api_gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Integer> redisTemplate;


    public void incrementFailedAttempts(String key) {
        try {
            if (redisTemplate.opsForValue().get(key) == null) {
                redisTemplate.opsForValue().set(key, 0);
            }
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error incrementing failed attempts in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Integer getFailedAttempts(String key) {
        try {
            Integer attempts = redisTemplate.opsForValue().get(key);
            return attempts != null ? attempts : 0;
        } catch (Exception e) {
            log.error("Error getting failed attempts from Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void resetFailedAttempts(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error resetting failed attempts in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

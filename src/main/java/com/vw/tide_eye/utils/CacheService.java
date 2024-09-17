package com.vw.tide_eye.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void flushRedisCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public void flushSpecificCache(String cacheName) {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
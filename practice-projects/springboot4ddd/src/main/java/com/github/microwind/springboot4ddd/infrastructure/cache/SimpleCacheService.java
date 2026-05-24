package com.github.microwind.springboot4ddd.infrastructure.cache;

import com.github.microwind.springboot4ddd.application.port.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 简单 Redis 缓存服务 —— {@link CacheService} 的 Redis 实现
 *
 * <p>实现 {@link CacheService} 端口，application 层通过接口注入，
 * 不直接依赖此类。
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleCacheService implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrSet(String key, Duration ttl, Supplier<T> supplier) {
        try {
            Object cachedObj = redisTemplate.opsForValue().get(key);
            if (cachedObj != null) {
                log.debug("缓存命中: {}", key);
                return (T) cachedObj;
            }

            log.debug("缓存未命中: {}", key);
            T data = supplier.get();

            if (data != null) {
                redisTemplate.opsForValue().set(key, data, ttl);
                log.debug("数据已缓存: {} TTL {}", key, ttl);
            }
            return data;
        } catch (Exception e) {
            log.error("缓存错误，键: {}，回退到数据源", key, e);
            return supplier.get();
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("缓存已删除: {}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: {}", key, e);
        }
    }
}


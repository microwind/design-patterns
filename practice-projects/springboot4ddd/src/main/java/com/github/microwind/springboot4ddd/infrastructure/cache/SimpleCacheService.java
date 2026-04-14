package com.github.microwind.springboot4ddd.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 简单Redis缓存服务 - 快速开始缓存
 * 
 * 使用示例：
 * 
 * // 从缓存获取或从数据库加载
 * UserResponse user = simpleCacheService.getOrSet(
 *     "user:123", 
 *     UserResponse.class, 
 *     Duration.ofMinutes(30), 
 *     () -> userRepository.findById(123L)
 * );
 * 
 * // 删除缓存
 * simpleCacheService.delete("user:123");
 * 
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 从缓存获取或从数据源设置
     */
    public <T> T getOrSet(String key, Class<T> clazz, Duration ttl, Supplier<T> supplier) {
        try {
            // 先尝试从缓存获取
            Object cachedObj = redisTemplate.opsForValue().get(key);
            if (cachedObj != null) {
                log.debug("缓存命中: {}", key);
                return clazz.cast(cachedObj);
            }

            // 缓存未命中，从数据源加载
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

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("缓存已删除: {}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: {}", key, e);
        }
    }
}

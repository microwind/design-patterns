package com.github.microwind.springboot4ddd.application.port;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 缓存服务端口（application 层）
 *
 * <p>application 通过该接口使用缓存能力，具体实现（Redis / Caffeine / 内存等）
 * 由 infrastructure 提供，避免 application 依赖具体缓存中间件。
 *
 * <p>反序列化所需的类型信息由实现侧序列化器（如
 * {@code GenericJackson2JsonRedisSerializer}）负责，端口契约不暴露 {@code Class<T>}。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface CacheService {

    /**
     * 从缓存获取；未命中则用 supplier 加载、写入缓存后返回。
     */
    <T> T getOrSet(String key, Duration ttl, Supplier<T> supplier);

    /**
     * 删除指定缓存键。
     */
    void delete(String key);
}

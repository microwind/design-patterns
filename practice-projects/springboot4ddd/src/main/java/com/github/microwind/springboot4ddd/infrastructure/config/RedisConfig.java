package com.github.microwind.springboot4ddd.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * 配置 RedisTemplate 序列化方式，集中管理缓存常量
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RedisConfig {

    /** 用户缓存键前缀 */
    public static final String USER_CACHE_PREFIX = "user:";

    /** 订单缓存键前缀 */
    public static final String ORDER_CACHE_PREFIX = "order:";

    /** 用户缓存过期时间（秒） */
    public static final long USER_CACHE_TTL = 1800; // 30分钟

    /** 订单缓存过期时间（秒） */
    public static final long ORDER_CACHE_TTL = 900; // 15分钟

    /**
     * 配置 RedisTemplate
     * Key 使用 String 序列化，Value 使用 JSON 序列化
     */
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        log.info("RedisTemplate 配置完成");
        return template;
    }
}

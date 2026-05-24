package com.github.microwind.springboot4ddd.application.port;

import java.time.Duration;

/**
 * 缓存策略常量（application 层）
 *
 * <p>"哪些数据缓存多久 / 用什么前缀" 是 application 关切，
 * 与具体中间件无关，因此从 infrastructure/config 搬到 application/port。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class CachePolicy {

    private CachePolicy() {
    }

    public static final String USER_KEY_PREFIX = "user:";
    public static final String ORDER_KEY_PREFIX = "order:";

    public static final Duration USER_TTL = Duration.ofMinutes(30);
    public static final Duration ORDER_TTL = Duration.ofMinutes(15);
}

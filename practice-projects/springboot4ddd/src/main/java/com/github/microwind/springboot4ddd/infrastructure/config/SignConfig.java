package com.github.microwind.springboot4ddd.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 签名配置类
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "sign")
@Data
public class SignConfig {

    private static final long DEFAULT_SIGNATURE_TTL = 600000L; // 10分钟

    public static final String HEADER_APP_CODE = "Sign-appCode";
    public static final String HEADER_SIGN = "Sign-sign";
    public static final String HEADER_TIME = "Sign-time";
    public static final String HEADER_PATH = "Sign-path";

    /**
     * 签名配置
     */
    private SignatureConfig signature = new SignatureConfig();

    /**
     * 需要缓存请求体的路径模式列表
     * <p>
     * 只有这些路径的请求才会被 CachedBodyFilter 缓存 body
     * 支持 Ant 风格路径模式：
     * - /api/order/** : 匹配 /api/order 下所有路径
     * - /api/*\/payment : 匹配 /api/任意单层/payment
     * - 留空则使用 header 检测模式（默认）
     */
    private List<String> cachedBodyPathPatterns;

    /**
     * 签名配置
     */
    @Data
    public static class SignatureConfig {
        /**
         * 签名有效期（毫秒）
         */
        private Long ttl = DEFAULT_SIGNATURE_TTL;

        /**
         * 默认是否使用参数签名
         * true: 签名计算包含请求参数
         * false: 签名计算不包含请求参数
         * 默认：false
         */
        private Boolean defaultWithParams = false;
    }

    /**
     * 获取签名有效期（毫秒）
     */
    public Long getSignatureTtl() {
        return signature != null ? signature.getTtl() : DEFAULT_SIGNATURE_TTL;
    }

    /**
     * 获取默认的 withParams 配置
     */
    public boolean isDefaultWithParams() {
        return signature != null && Boolean.TRUE.equals(signature.getDefaultWithParams());
    }
}

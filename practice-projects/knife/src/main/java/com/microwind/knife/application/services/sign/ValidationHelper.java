package com.microwind.knife.application.services.sign;

import org.springframework.stereotype.Component;

/**
 * 校验助手类
 * <p>
 * 提供公共的校验方法，用于签名和动态盐值验证服务
 */
@Component
public class ValidationHelper {

    /**
     * 校验时间戳（防止时间回拨攻击）
     *
     * @param timestamp 时间戳
     * @param timeType  时间类型描述（如："签名"、"动态盐值"）
     * @throws IllegalArgumentException 时间戳为空或大于当前时间
     */
    public void validateTimestamp(Long timestamp, String timeType) {
        if (timestamp == null) {
            throw new IllegalArgumentException(timeType + "时间戳不能为空");
        }

        long now = System.currentTimeMillis();
        if (timestamp > now) {
            throw new IllegalArgumentException(
                    timeType + "时间不能大于当前时间：" + timestamp
            );
        }
    }

    /**
     * 校验TTL（有效期）
     *
     * @param timestamp      时间戳
     * @param ttl            有效期（毫秒）
     * @param validationType 校验类型描述（如："签名"、"动态盐值"）
     * @throws IllegalArgumentException 超过有效期
     */
    public void validateTtl(Long timestamp, Long ttl, String validationType) {
        long now = System.currentTimeMillis();
        if ((now - timestamp) > ttl) {
            throw new IllegalArgumentException(
                    validationType + "已超过有效期：" + timestamp
            );
        }
    }

    /**
     * 校验字符串参数完整性
     *
     * @param params 参数列表
     * @return true-所有参数都不为null，false-至少有一个参数为null
     */
    public boolean validateParams(String... params) {
        for (String param : params) {
            if (param == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验Long参数完整性
     *
     * @param params 参数列表
     * @return true-所有参数都不为null，false-至少有一个参数为null
     */
    public boolean validateParams(Long... params) {
        for (Long param : params) {
            if (param == null) {
                return false;
            }
        }
        return true;
    }
}

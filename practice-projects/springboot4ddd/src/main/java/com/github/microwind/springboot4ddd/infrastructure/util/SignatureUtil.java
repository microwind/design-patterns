package com.github.microwind.springboot4ddd.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 签名工具类
 * 提供SHA-256签名功能
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
public class SignatureUtil {

    /**
     * SHA-256 哈希计算
     *
     * @param input 待哈希的字符串
     * @return SHA-256 哈希值（十六进制小写字符串）
     */
    public static String sha256(String input) {
        try {
            String hash = DigestUtils.sha256Hex(input);
            log.debug("SHA-256 hash calculated for input length: {}", input.length());
            return hash;
        } catch (Exception e) {
            log.error("SHA-256 calculation failed: input={}", input, e);
            throw new IllegalStateException("SHA-256 algorithm error", e);
        }
    }

    /**
     * 构建签名源字符串（简化版，不含参数）
     *
     * @param appCode   应用编码
     * @param secretKey 密钥
     * @param path      接口路径
     * @param timestamp 时间戳
     * @return 签名源字符串
     */
    public static String buildSignSource(String appCode, String secretKey, String path, Long timestamp) {
        return appCode + secretKey + path + timestamp;
    }
}

package com.github.microwind.springboot4ddd.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

    /**
     * 构建签名源字符串（带参数）
     *
     * @param appCode    应用编码
     * @param secretKey  密钥
     * @param path       接口路径
     * @param timestamp  时间戳
     * @param parameters 请求参数
     * @return 签名源字符串
     */
    public static String buildSignSourceWithParams(String appCode, String secretKey, String path, Long timestamp,
                                                    Map<String, Object> parameters) {
        String paramStr = buildParameterString(parameters);
        return paramStr + appCode + secretKey + path + timestamp;
    }

    /**
     * 构建参数字符串（按字典序排序）
     * 规则：
     * 1. 排除空串("")和null值
     * 2. 排除复杂类型（非基础类型/字符串）
     * 3. 按字典序排序(ASCII升序)
     * 4. 拼接格式：key1=value1&key2=value2&...
     */
    public static String buildParameterString(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        // 按字典序排序参数键
        List<String> sortedKeys = new ArrayList<>(parameters.keySet());
        Collections.sort(sortedKeys);

        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String key : sortedKeys) {
            Object paramValue = parameters.get(key);

            // 跳过 null / 复杂类型
            if (paramValue == null || isComplexType(paramValue)) {
                continue;
            }

            String valueStr = paramValue.toString();
            // 跳过空串
            if (valueStr.isEmpty()) {
                continue;
            }

            if (!first) {
                builder.append("&");
            }
            first = false;
            builder.append(key).append("=").append(valueStr);
        }

        log.debug("Built parameter string: {}", builder);
        return builder.toString();
    }

    /**
     * 判断是否为复杂类型
     */
    private static boolean isComplexType(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        // 基础类型/包装类/字符串/枚举 视为简单类型
        return !(clazz.isPrimitive()
                || obj instanceof String
                || obj instanceof Number
                || obj instanceof Boolean
                || obj instanceof Character
                || obj instanceof Enum<?>
                || obj instanceof Date);
    }

    /**
     * 生成签名（不带参数）
     */
    public static String generateSign(String appCode, String secretKey, String path, Long timestamp) {
        String signSource = buildSignSource(appCode, secretKey, path, timestamp);
        log.info("Generating sign - source: {}", signSource);
        return sha256(signSource);
    }

    /**
     * 生成签名（带参数）
     */
    public static String generateSignWithParams(String appCode, String secretKey, String path, Long timestamp,
                                                 Map<String, Object> parameters) {
        String signSource = buildSignSourceWithParams(appCode, secretKey, path, timestamp, parameters);
        log.info("Generating sign with params - source: {}", signSource);
        return sha256(signSource);
    }

    /**
     * 验证签名（不带参数）
     */
    public static boolean verifySign(String appCode, String secretKey, String path, Long timestamp, String sign) {
        String expectedSign = generateSign(appCode, secretKey, path, timestamp);
        return expectedSign.equals(sign);
    }

    /**
     * 验证签名（带参数）
     */
    public static boolean verifySignWithParams(String appCode, String secretKey, String path, Long timestamp,
                                               Map<String, Object> parameters, String sign) {
        String expectedSign = generateSignWithParams(appCode, secretKey, path, timestamp, parameters);
        return expectedSign.equals(sign);
    }
}


package com.microwind.knife.utils;

import cn.hutool.crypto.SmUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 签名工具类 - 提供 MD5、SHA1、SHA256 和 SM3 签名功能
 * 用于数据同步时的签名生成和验证
 */
@Slf4j
public class SignatureUtil {
    // 定义支持的签名算法
    public enum Algorithm {
        MD5, SHA1, SHA256, SM3
    }

    /**
     * 通用签名方法（抽取重复逻辑）
     */
    public static String sign(Map<String, Object> parameters, String appCode, String secretKey, Long time, Algorithm algorithm) {
        if (parameters == null) {
            throw new IllegalArgumentException("待签名参数不能为空");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("签名密钥不能为空");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("签名算法不能为空");
        }

        // 构建签名源字符串（确保非null）
        String signatureSource = buildSignatureSource(parameters);
        if (appCode != null && !appCode.isEmpty()) {
            signatureSource += appCode;
        }
        if (secretKey != null && !secretKey.isEmpty()) {
            signatureSource += secretKey;
        }
        if (time != null) {
            signatureSource += time;
        }
        log.info("[SignatureUtil.sign] 签名算法:（{}） 源字符串:{}", algorithm.name(), signatureSource);

        // 按算法生成签名（统一UTF-8编码）
        byte[] sourceBytes = signatureSource.getBytes(StandardCharsets.UTF_8);
        try {
            return switch (algorithm) {
                case MD5 -> DigestUtils.md5Hex(sourceBytes);
                case SHA1 -> DigestUtils.sha1Hex(sourceBytes);
                case SHA256 -> DigestUtils.sha256Hex(sourceBytes);
                case SM3 -> SmUtil.sm3(Arrays.toString(sourceBytes)); // 直接传入字节数组，避免编码问题
            };
        } catch (Exception e) {
            log.error("{} 签名计算失败，签名源: {}", algorithm.name(), signatureSource, e);
            throw new RuntimeException("签名算法执行失败: " + algorithm.name(), e);
        }
    }

    public static String sign(Map<String, Object> parameters, String secretKey, Algorithm algorithm) {
        return sign(parameters, null, secretKey, null, algorithm);
    }

    // 简化各算法的快捷方法
    public static String md5Sign(Map<String, Object> parameters, String secretKey) {
        return sign(parameters, secretKey, Algorithm.MD5);
    }

    public static String sha1Sign(Map<String, Object> parameters, String secretKey) {
        return sign(parameters, secretKey, Algorithm.SHA1);
    }

    public static String sha256Sign(Map<String, Object> parameters, String secretKey) {
        return sign(parameters, secretKey, Algorithm.SHA256);
    }

    public static String sm3Sign(Map<String, Object> parameters, String appCode, String secretKey, Long time) {
        return sign(parameters, appCode, secretKey, time, Algorithm.SM3);
    }

    public static String sm3Sign(Map<String, Object> parameters, String secretKey) {
        return sign(parameters, secretKey, Algorithm.SM3);
    }

    /**
     * 构建签名源字符串（修复核心问题）
     * 规则：
     * 1. 排除空串("")和null值
     * 2. 排除复杂类型（非基础类型/字符串）
     * 3. 按字典序排序(ASCII升序)
     * 4. 拼接格式：key1=value1&key2=value2&...
     * 5. 即使无业务参数，也拼接密钥（避免空签名源）
     */
    public static String buildSignatureSource(Map<String, Object> parameters) {
        if (parameters.isEmpty()) {
            return "";
        }
        // 按字典序排序参数键
        List<String> sortedKeys = new ArrayList<>(parameters.keySet());
        Collections.sort(sortedKeys);

        StringBuilder signatureBuilder = new StringBuilder();
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
                signatureBuilder.append("&");
            }
            first = false;
            signatureBuilder.append(key).append("=").append(valueStr);
        }

        log.info("[SignatureUtil.buildSignatureSource] 签名源字符串: {}", signatureBuilder);
        return signatureBuilder.toString();
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
                // 日期类型特殊处理（toString()结果稳定）
                || obj instanceof Date);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        /* === 测试例子1：正常参数 === */
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("userName", "张三");
        requestParams.put("emptyNo", ""); // 空串不参与签名
        requestParams.put("mobile", "19212341234");
        requestParams.put("emptyParam", null); // null不参与签名
        requestParams.put("userId", "321123");
        requestParams.put("nonce", "5w5sd2fe9iexadfsvafsadact3wh");
        requestParams.put("list", new ArrayList<>()); // 复杂类型不参与签名
        requestParams.put("object", new Object()); // 复杂类型不参与签名
        requestParams.put("timestamp", "1650713278548");
        String secretKey = "your_secret_key_1";

        String md5Sign = md5Sign(requestParams, secretKey);
        String sha1Sign = sha1Sign(requestParams, secretKey);
        String sha256Sign = sha256Sign(requestParams, secretKey);
        String sm3Sign = sm3Sign(requestParams, secretKey);

        System.out.println("=== 正常参数测试 ===");
        System.out.println("MD5 签名结果: " + md5Sign);
        System.out.println("SHA1 签名结果: " + sha1Sign);
        System.out.println("SHA256 签名结果: " + sha256Sign);
        System.out.println("SM3 签名结果: " + sm3Sign);

        /* === 测试例子2：空参数集（边界测试） === */
        Map<String, Object> emptyParams = new HashMap<>();
        String emptyMd5 = md5Sign(emptyParams, secretKey);
        System.out.println("\n=== 空参数测试 ===");
        System.out.println("空参数MD5签名: " + emptyMd5);
    }
}
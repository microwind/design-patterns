package com.microwind.knife.utils;

import cn.hutool.crypto.SmUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * 签名工具类 - 提供 MD5、SHA1 和 SM3 签名功能
 * 用于数据同步时的签名生成和验证
 */
@Slf4j
public class SignatureUtil {

    /**
     * 生成 MD5 签名
     *
     * @param parameters 待签名参数
     * @param secretKey  签名密钥
     * @return MD5 签名字符串
     */
    public static String md5Sign(Map<String, Object> parameters, String secretKey) {
        if (parameters == null || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("参数和密钥不能为空");
        }
        String signatureSource = buildSignatureSource(parameters, secretKey);
        log.debug("[SignatureUtil.md5Sign] signatureSource = {}", signatureSource);
        return signatureSource == null ? null : DigestUtils.md5Hex(signatureSource);
    }

    /**
     * 生成 SHA1 签名
     *
     * @param parameters 待签名参数
     * @param secretKey  签名密钥
     * @return SHA1 签名字符串
     */
    public static String sha1Sign(Map<String, Object> parameters, String secretKey) {
        if (parameters == null || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("参数和密钥不能为空");
        }
        String signatureSource = buildSignatureSource(parameters, secretKey);
        log.debug("[SignatureUtil.sha1Sign] signatureSource = {}", signatureSource);
        return signatureSource == null ? null : DigestUtils.sha1Hex(signatureSource);
    }

    /**
     * 生成 SM3 签名
     *
     * @param parameters 待签名参数
     * @param secretKey  签名密钥
     * @return SM3 签名字符串
     */
    public static String sm3Sign(Map<String, Object> parameters, String secretKey) {
        if (parameters == null || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("参数和密钥不能为空");
        }
        String signatureSource = buildSignatureSource(parameters, secretKey);
        log.debug("[SignatureUtil.sm3Sign] signatureSource = {}", signatureSource);
        return signatureSource == null ? null : SmUtil.sm3(signatureSource);
    }

    /**
     * 构建签名源字符串
     * <p>
     * 规则：
     * 1. 排除空串("")和null值
     * 2. 排除复杂类型(Map、List、Array)
     * 3. 按字典序排序(ASCII升序)
     * 4. 拼接格式：key1=value1&key2=value2&...&key=密钥
     */
    private static String buildSignatureSource(Map<String, Object> parameters, String secretKey) {
        // 按字典序排序参数键
        List<String> sortedKeys = new ArrayList<>(parameters.keySet());
        Collections.sort(sortedKeys);

        StringBuilder signatureBuilder = new StringBuilder();
        for (String key : sortedKeys) {
            Object paramValue = parameters.get(key);
            String valueStr = null;

            if (paramValue != null) {
                // 跳过复杂类型
                if (paramValue instanceof Map || paramValue instanceof List || paramValue.getClass().isArray()) {
                    continue;
                } else {
                    valueStr = paramValue.toString();
                }
            }

            // 空串和null不参与签名
            if (valueStr != null && !valueStr.isEmpty()) {
                signatureBuilder.append(key).append("=").append(valueStr).append("&");
            }
        }

        // 最后拼接密钥
        if (!signatureBuilder.isEmpty()) {
            signatureBuilder.append("key=").append(secretKey);
        }

        log.debug("[SignatureUtil.buildSignatureSource] signatureSource = {}", signatureBuilder);
        return !signatureBuilder.isEmpty() ? signatureBuilder.toString() : null;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        /* === 测试例子1 === */
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("userName", "张三");
        requestParams.put("emptyNo", ""); // 空串不参与签名
        requestParams.put("mobile", "19212341234");
        requestParams.put("userStatus", "1");
        requestParams.put("userLevel", "1");
        requestParams.put("nonce", "d%442abcdef12a");
        requestParams.put("timestamp", "1650713278548");
        String secretKey = "your_secret_key_1";
        String md5Sign = md5Sign(requestParams, secretKey);
        String sha1Sign = sha1Sign(requestParams, secretKey);
        String sm3Sign = sm3Sign(requestParams, secretKey);
        // 3daf96a0f25ea2dae16ebd15238fa90c
        System.out.println("requestParams MD5 签名结果: " + md5Sign);
        // 679ec8da98cb8545f081e12633b4f5f3254fa48f
        System.out.println("requestParams SHA1 签名结果: " + sha1Sign);
        // 签名结果: d43f74f66f50911b99095cd61c5d205157383fa614fed7f93ec583cba58cab8b
        System.out.println("requestParams SM3 签名结果: " + sm3Sign);

        /* === 测试例子2 === */
        Map<String, Object> requestParams2 = new HashMap<>();
        requestParams2.put("emptyParam", null); // null和空值，不参与签名
        requestParams2.put("userId", "321123");
        requestParams2.put("nonce", "5w5sd2fe9iexadfsvafsadact3wh");
        requestParams2.put("list", new ArrayList<>());
        requestParams2.put("timestamp", "1650713278548");

        String secretKey2 = "your_secret_key_2";
        String sha1Sign2 = sha1Sign(requestParams2, secretKey2);
        String sm3Sign2 = sm3Sign(requestParams2, secretKey2);

        // 450779be64073ef6933553dfff74959fc7b2ab31
        System.out.println("requestParams2 SHA1 签名: " + sha1Sign2);

        // 签名结果: 627e7d2f96b16a27079fd19ee1957b73ca2497a3d07102cc7d81677a894eacbb
        System.out.println("requestParams2 SM3 签名: " + sm3Sign2);

        String signStr = "nonce=5w5sd2fe9iexadfsvafsadact3wh&timestamp=1650713278548&userId=321123&key=your_secret_key_2";
        // 627e7d2f96b16a27079fd19ee1957b73ca2497a3d07102cc7d81677a894eacbb
        System.out.println("requestParams2 apiauth：" + SmUtil.sm3(signStr));
    }
}

package com.microwind.knife.utils;

import cn.hutool.crypto.SmUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * SignatureUtil - 提供 MD5 和 SM3 签名工具示例
 * 本文件用于数据同步时的sign生成
 * <p>
 * Maven 依赖：
 *
 * <dependency>
 * <groupId>cn.hutool</groupId>
 * <artifactId>hutool-all</artifactId>
 * <version>5.8.20</version>
 * </dependency>
 * <dependency>
 * BouncyCastle (SM3算法依赖)
 * <groupId>org.bouncycastle</groupId>
 * <artifactId>bcprov-jdk15on</artifactId>
 * <version>1.70</version>
 * </dependency>
 *
 * <dependency>
 * <groupId>commons-codec</groupId>
 * <artifactId>commons-codec</artifactId>
 * <version>1.16.0</version>
 * </dependency>
 *
 */
@Slf4j
public class SignatureUtil {

    public static String md5Sign(Map<String, Object> params, String key) {
        if (params == null || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("参数和key不能为空。");
        }
        String originStr = buildOriginStr(params, key);
        log.debug("[SignatureUtil.md5Sign] originStr = {}", originStr);
        return originStr == null ? null : DigestUtils.md5Hex(originStr);
    }

    public static String sm3Sign(Map<String, Object> params, String key) {
        if (params == null || key == null || key.isEmpty()) {
            throw new IllegalArgumentException("参数和key不能为空。");
        }
        String originStr = buildOriginStr(params, key);
        log.info("[SignatureUtil.sm3Sign] originStr = {}", originStr);
        return originStr == null ? null : SmUtil.sm3(originStr);
    }

    /**
     * 构造签名原始字符串
     * 规则：
     * 1. 排除 apiauth 字段（会修改传入的 params）
     * 2. 空串 ("") 和 null 不参与签名
     * 3. 字典序排序（ASCII 升序）
     * 4. 拼接为：key1=value1&key2=value2&...&key=密钥
     */
    private static String buildOriginStr(Map<String, Object> params, String key) {
        // 排除 apiauth 字段
        params.remove("apiauth");

        // 按字典序（ASCII 升序）排序
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            Object obj = params.get(k);
            String v = null;

            if (obj != null) {
                // 目前参数不含复杂类型，若含有可对Map、List等复杂类型返回固定字符串
                if (obj instanceof Map || obj instanceof List || obj.getClass().isArray()) {
                    v = "[object]";
                } else {
                    v = obj.toString();
                }
            }

            // 空串和 null 不参与签名
            if (v != null && !v.isEmpty()) {
                sb.append(k).append("=").append(v).append("&");
            }
        }

        // 最后拼接密钥
        if (!sb.isEmpty()) {
            sb.append("key=").append(key);
        }

        log.debug("[SignatureUtil.buildOriginStr] originStr = {}", sb);
        return !sb.isEmpty() ? sb.toString() : null;
    }

    public static void main(String[] args) {

        /* 测试例子1 */
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("partnerCode","HBSW");
        requestParams.put("userType", "1");
        requestParams.put("userName","张三");
        requestParams.put("partnerNo",""); // 空串不参与签名
        requestParams.put("mobile", "19212341234");
        requestParams.put("partnerUserId","884828273082823");
        requestParams.put("sex","0");
        requestParams.put("provinceName","河北省");
        requestParams.put("cityName","邯郸市");
        requestParams.put("districtName","临漳县");
        requestParams.put("address","河北省邯郸市临漳县香菜营乡邺镇村市场路18号");
        requestParams.put("encryptType","1");
        requestParams.put("birthday","1995.07.08");
        requestParams.put("deviceCode","SN23249988372");
        requestParams.put("deviceBrand","创维");
        requestParams.put("deviceModel","5AA46_BG22");
        requestParams.put("userStatus","1");
        requestParams.put("userLevel", "1");
        requestParams.put("businessCode","tvallinone");
        requestParams.put("remark", ""); // 空串和 null 不参与签名
        // System.out.println("random nonstr: " + UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        requestParams.put("nonstr", "5w5f8fe9iexaact3wh");
        // System.out.println("current timestamp: " + String.valueOf(System.currentTimeMillis()));
        requestParams.put("timestamp", "1650713278548");
        String key = "HBSW_123456"; // 秘钥单独提供
        String sm3Sign = sm3Sign(requestParams, key);
        // 签名结果: b9ea764a11371bf2eacf33564ee173353824d00d95b568af45d74b5d4396d640
        System.out.println("requestParams SM3 签名结果: " + sm3Sign);

        /* 测试例子2 */
        Map<String, Object> requestParams2 = new HashMap<>();
        requestParams2.put("code", "0");
        requestParams2.put("eventId", "123");
        requestParams2.put("msg", "123");
        requestParams2.put("emptyParam", null); // null和空值，不参与签名
        requestParams2.put("userId", "123");
        requestParams2.put("nonstr", "5w5f8fe9iexaact3wh");
        requestParams2.put("timestamp", "1650713278548");

        String key2 = "7jm3ov4mp2stanssvq"; // 秘钥单独提供
        String sm3Sign2 = sm3Sign(requestParams2, key2);
        // 签名结果: fe5efc0ab37418f0925cbf790a80aadcd0da8ffb36a98852b194cd6ae2a292fe
        System.out.println("requestParams2 SM3 签名: " + sm3Sign2);

        /* 测试例子3 */
        Map<String, Object> requestParams3 = new HashMap<>();
        requestParams3.put("partnerCode", "GDSW");
        requestParams3.put("partnerOrderNo", "2025102801");

        // 随机时间戳（未来时间）
        requestParams3.put("partnerOrderTime", "1764834817870");

        requestParams3.put("productNo", "xn_1445253726877229069");
        requestParams3.put("amount", "1");
        requestParams3.put("mobile", "19299999999");

        requestParams3.put("partnerUserId", "760043077516");
        requestParams3.put("partnerProductId", "127540");
        requestParams3.put("partnerProductName", "基本包");
        requestParams3.put("partnerProductPrice", "10000");
        requestParams3.put("partnerProductType", "1");
        requestParams3.put("behavior", "1");
        requestParams3.put("encryptType", "1");
        requestParams3.put("businessCode", "tvallinone");
        requestParams3.put("nonstr", "526434");
        requestParams3.put("timestamp", "1764834817871");
        // 你自己的签名密钥
        String secret = "W6BZElooSgFOxRFRvnes";

        // ===============================
        // 2. 计算 apiauth
        // ===============================
        String sign = sm3Sign(requestParams3, secret);
        requestParams3.put("apiauth", sign);

        // ===============================
        // 3. 输出 JSON
        // ===============================
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(requestParams3);
            System.out.println("生成的 JSON：");
            System.out.println(json);
        } catch (Exception e) {
            System.out.println(e);
        }

        String signStr = "amount=1&behavior=1&businessCode=tvallinone&encryptType=1&mobile=19299999999&nonstr=526434&partnerCode=GDSW&partnerOrderNo=2025102801&partnerOrderTime=1764834817870&partnerProductId=127540&partnerProductName=基本包&partnerProductPrice=10000&partnerProductType=1&partnerUserId=760043077516&productNo=xn_1445253726877229069&timestamp=1764834817871&key=W6BZElooSgFOxRFRvnes";
        System.out.println("requestParams3 apiauth：" + SmUtil.sm3(signStr));

    }
}

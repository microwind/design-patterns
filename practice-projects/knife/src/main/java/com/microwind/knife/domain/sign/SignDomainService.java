package com.microwind.knife.domain.sign;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

@Service
public class SignDomainService {
    // 生成动态盐值（核心算法）
    public DynamicSalt generateDynamicSalt(String appCode, String path, String interfaceSalt) {
        long generateTime = System.currentTimeMillis();
        String saltSource = appCode + path + interfaceSalt + generateTime;
        String saltValue = Arrays.toString(DigestUtils.md5Digest(saltSource.getBytes()));
        return new DynamicSalt(appCode, path, saltValue, generateTime);
    }

    // 校验动态盐值有效性
    public boolean validateDynamicSalt(String appCode, String path, String interfaceSalt,
                                       String dynamicSalt, Long generateTime) {
        String expectedSalt = DigestUtils.md5DigestAsHex((appCode + path + interfaceSalt + generateTime).getBytes());
        return expectedSalt.equals(dynamicSalt);
    }

    // 生成签名（核心算法）
    public Sign generateSign(String appCode, String path, String appSecret, String interfaceSalt) {
        long timestamp = System.currentTimeMillis();
        String signSource = appCode + appSecret + timestamp + path + interfaceSalt; // 按约定算法
        String signValue = DigestUtils.md5DigestAsHex(signSource.getBytes());
        return new Sign(appCode, path, signValue, timestamp);
    }

    // 校验签名有效性
    public boolean validateSign(String appCode, String path, String appSecret, String interfaceSalt,
                                String signValue, Long timestamp) {
        String expectedSign = DigestUtils.md5DigestAsHex((appCode + appSecret + timestamp + path + interfaceSalt).getBytes());
        return expectedSign.equals(signValue);
    }
}
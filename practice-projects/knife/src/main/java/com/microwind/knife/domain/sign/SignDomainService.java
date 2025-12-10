package com.microwind.knife.domain.sign;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

@Service
public class SignDomainService {
    // 生成动态盐值（核心算法）
    public DynamicSalt generateDynamicSalt(String appKey, String path, String interfaceSalt) {
        long generateTime = System.currentTimeMillis();
        String saltSource = appKey + path + interfaceSalt + generateTime;
        String saltValue = Arrays.toString(DigestUtils.md5Digest(saltSource.getBytes()));
        return new DynamicSalt(appKey, path, saltValue, generateTime);
    }

    // 校验动态盐值有效性
    public boolean validateDynamicSalt(String appKey, String path, String interfaceSalt,
                                       String dynamicSalt, Long generateTime) {
        String expectedSalt = DigestUtils.md5DigestAsHex((appKey + path + interfaceSalt + generateTime).getBytes());
        return expectedSalt.equals(dynamicSalt);
    }

    // 生成签名（核心算法）
    public Sign generateSign(String appKey, String path, String appSecret, String interfaceSalt) {
        long timestamp = System.currentTimeMillis();
        String signSource = appKey + appSecret + timestamp + path + interfaceSalt; // 按约定算法
        String signValue = DigestUtils.md5DigestAsHex(signSource.getBytes());
        return new Sign(appKey, path, signValue, timestamp);
    }

    // 校验签名有效性
    public boolean validateSign(String appKey, String path, String appSecret, String interfaceSalt,
                                String signValue, Long timestamp) {
        String expectedSign = DigestUtils.md5DigestAsHex((appKey + appSecret + timestamp + path + interfaceSalt).getBytes());
        return expectedSign.equals(signValue);
    }
}
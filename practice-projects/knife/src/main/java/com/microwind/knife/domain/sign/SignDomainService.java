package com.microwind.knife.domain.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.SignDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class SignDomainService {
    private final SignConfig signConfig;

    // 生成动态盐值（核心算法）
    public DynamicSalt generateDynamicSalt(String appCode, String apiPath,
                                           String interfaceSalt, Long saltTimestamp) {
        // 字符串拼接，再计算hash值，需与校验算法一致
        String saltSource = appCode + apiPath + interfaceSalt + saltTimestamp;
        String dynamicSaltValue = DigestUtils.md5DigestAsHex(
                saltSource.getBytes(StandardCharsets.UTF_8)
        );

        // 计算过期时间（TTL 单位：毫秒），当前时间加默认时长
        Long ttl = signConfig.getDynamicSaltTtl();
        LocalDateTime expireTime = ttl == null
                ? null
                : Instant.ofEpochMilli(saltTimestamp + ttl)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return new DynamicSalt(appCode, apiPath, dynamicSaltValue, expireTime, saltTimestamp);
    }

    public DynamicSalt generateDynamicSalt(DynamicSaltDTO dynamicSaltDTO, String interfaceSalt) {
        return generateDynamicSalt(dynamicSaltDTO.getAppCode(),
                interfaceSalt,
                dynamicSaltDTO.getApiPath(),
                dynamicSaltDTO.getSaltTimestamp());
    }

    // 校验动态盐值有效性
    public boolean validateDynamicSalt(String appCode, String apiPath, String interfaceSalt,
                                       String dynamicSalt, Long saltTimestamp) {
        // 字符串拼接，再计算hash值，需与生成时算法一致
        String expectedSaltSource = appCode + apiPath + interfaceSalt + saltTimestamp;
        String expectedSaltValue = DigestUtils.md5DigestAsHex(
                expectedSaltSource.getBytes(StandardCharsets.UTF_8)
        );
        return expectedSaltValue.equals(dynamicSalt);
    }

    // 校验动态盐值有效性，传入对象
    public boolean validateDynamicSalt(DynamicSaltDTO dto, String interfaceSalt) {
        return validateDynamicSalt(dto.getAppCode(),
                dto.getApiPath(),
                interfaceSalt,
                dto.getDynamicSalt(),
                dto.getSaltTimestamp());
    }

    // 生成签名（核心算法）
    public Sign generateSign(String appCode, String secretKey, String path) {
        long timestamp = System.currentTimeMillis();
        // 字符串拼接，再计算hash值，需与校验算法一致
        String signSource = appCode + secretKey + timestamp + path;
        String signValue = DigestUtils.md5DigestAsHex(signSource.getBytes(StandardCharsets.UTF_8));
        LocalDateTime expireTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp + signConfig.getSignatureTtl()),
                ZoneId.systemDefault()
        );
        return new Sign(appCode, path, signValue, timestamp, expireTime);
    }

    public Sign generateSign(SignDTO signDTO, String secretKey) {
        return generateSign(signDTO.getAppCode(), secretKey, signDTO.getApiPath());
    }

    // 校验签名有效性 (算法与生成签名时一致)
    public boolean validateSign(String appCode, String secretKey, String path,
                                String signValue, Long timestamp) {
        // 字符串拼接，再计算hash值，需与生成时算法一致
        String signSource = appCode + secretKey + timestamp + path;
        String expectedSign = DigestUtils.md5DigestAsHex(signSource.getBytes(StandardCharsets.UTF_8));
        return expectedSign.equals(signValue);
    }

    // 校验签名有效性，传递签名对象
    public boolean validateSign(SignDTO dto, String secretKey) {
        return validateSign(dto.getAppCode(),
                secretKey,
                dto.getApiPath(),
                dto.getSignValue(),
                dto.getTimestamp());
    }
}
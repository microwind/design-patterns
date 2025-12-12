package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignService {
    private final SignDomainService signDomainService;
    private final ApiAuthConfig signConfig;

    // 生成签名（含权限和盐值校验）
    public Sign generate(String appKey, String path, String dynamicSalt, Long saltGenerateTime) {
        // 1. 校验appKey是否存在
        ApiAuthConfig.AppConfig appConfig = signConfig.getAppByKey(appKey);
        if (appConfig == null) {
            throw new IllegalArgumentException("无效的appKey：" + appKey);
        }

        // 2. 校验是否有权访问签名接口和提交接口
        String signGeneratePath = "/apiauth/apiauth-generate";
        if (!signConfig.hasPermission(appKey, signGeneratePath)
                || !signConfig.hasPermission(appKey, path)) {
            throw new SecurityException("appKey无接口访问权限");
        }

        // 3. 获取接口盐值
        String interfaceSalt = signConfig.getInterfaceSalt(path);
        if (interfaceSalt == null) {
            throw new IllegalArgumentException("接口路径不存在：" + path);
        }

        // 4. 校验动态盐值有效性
        if (!signDomainService.validateDynamicSalt(appKey, path, interfaceSalt, dynamicSalt, saltGenerateTime)) {
            throw new SecurityException("动态盐值无效");
        }

        // 5. 校验盐值时效性
        long dynamicSaltTtl = signConfig.getValidation().getDynamicSaltTtl();
        if (System.currentTimeMillis() - saltGenerateTime > dynamicSaltTtl) {
            throw new SecurityException("动态盐值已过期");
        }

        // 6. 生成签名
        return signDomainService.generateSign(appKey, path, appConfig.getAppSecret(), interfaceSalt);
    }
}
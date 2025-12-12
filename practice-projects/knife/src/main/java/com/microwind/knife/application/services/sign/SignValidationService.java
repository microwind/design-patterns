package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 签名验证服务 - 基于配置文件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignValidationService {
    private final ApiAuthConfig apiAuthConfig;
    private final SignDomainService signDomainService;

    /**
     * 验证请求的权限、时效和签名
     * @param appKey 应用Key
     * @param path 接口路径
     * @param sign 签名
     * @param signTime 签名时间戳
     * @return 验证是否通过
     * @throws SecurityException 验证失败时抛出异常
     */
    public boolean validateRequest(String appKey, String path, String sign, Long signTime) {
        log.info("开始验证签名 - appKey: {}, path: {}, signTime: {}", appKey, path, signTime);

        // 1. 校验appKey是否存在
        ApiAuthConfig.AppConfig appConfig = apiAuthConfig.getAppByKey(appKey);
        if (appConfig == null) {
            log.error("无效的appKey: {}", appKey);
            throw new SecurityException("无效的appKey：" + appKey);
        }

        // 2. 校验权限 - 检查是否有访问该接口的权限
        if (!apiAuthConfig.hasPermission(appKey, path)) {
            log.error("appKey {} 无权限访问接口: {}", appKey, path);
            throw new SecurityException("无权限访问该接口");
        }

        // 3. 校验时效性
        long currentTime = System.currentTimeMillis();
        long signTtl = apiAuthConfig.getValidation().getSignTtl();
        if (currentTime - signTime > signTtl) {
            log.error("签名已过期 - 当前时间: {}, 签名时间: {}, 有效期: {}ms", currentTime, signTime, signTtl);
            throw new SecurityException("签名已过期");
        }

        // 4. 校验签名
        String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
        if (interfaceSalt == null) {
            log.error("未配置接口盐值 - path: {}", path);
            throw new SecurityException("未配置接口盐值");
        }

        boolean isValid = signDomainService.validateSign(
                appKey,
                path,
                appConfig.getAppSecret(),
                interfaceSalt,
                sign,
                signTime
        );

        if (!isValid) {
            log.error("签名验证失败 - appKey: {}, path: {}", appKey, path);
            throw new SecurityException("签名无效");
        }

        log.info("签名验证成功 - appKey: {}, path: {}", appKey, path);
        return true;
    }

    /**
     * 验证动态盐值的权限和时效
     */
    public boolean validateDynamicSaltPermission(String appKey, String targetPath) {
        // 1. 校验appKey是否存在
        ApiAuthConfig.AppConfig appConfig = apiAuthConfig.getAppByKey(appKey);
        if (appConfig == null) {
            throw new SecurityException("无效的appKey：" + appKey);
        }

        // 2. 校验是否有访问目标接口的权限
        if (!apiAuthConfig.hasPermission(appKey, targetPath)) {
            throw new SecurityException("无权限访问目标接口");
        }

        return true;
    }
}

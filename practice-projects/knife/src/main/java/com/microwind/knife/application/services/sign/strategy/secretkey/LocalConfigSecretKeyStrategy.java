package com.microwind.knife.application.services.sign.strategy.secretkey;

import com.microwind.knife.application.config.ApiAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 本地配置文件秘钥获取策略
 * <p>
 * 从本地配置文件（apiauth-config.yml）读取应用秘钥
 * <p>
 * 适用场景：小型项目，调用方较少（< 50个）
 */
@Component("localConfigSecretKeyStrategy")
@RequiredArgsConstructor
public class LocalConfigSecretKeyStrategy extends AbstractSecretKeyStrategy {
    private final ApiAuthConfig apiAuthConfig;

    @Override
    protected void checkPermission(String appCode, String path) {
        if (apiAuthConfig.noPermission(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }
    }

    @Override
    protected String doGetSecretKey(String appCode, String path) {
        ApiAuthConfig.AppConfig appConfig = apiAuthConfig.getAppByKey(appCode);
        if (appConfig == null) {
            throw new IllegalArgumentException("应用不存在，appCode：" + appCode);
        }
        return appConfig.getAppSecret();
    }
}

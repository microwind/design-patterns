package com.microwind.knife.application.services.sign.strategy.interfacesalt;

import com.microwind.knife.application.config.ApiAuthConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 本地配置文件接口盐值获取策略
 * <p>
 * 从本地配置文件（apiauth-config.yml）读取接口固定盐值
 * <p>
 * 适用场景：小型项目，调用方较少（< 50个）
 */
@Component("localConfigInterfaceSaltStrategy")
@RequiredArgsConstructor
public class LocalConfigInterfaceSaltStrategy extends AbstractInterfaceSaltStrategy {
    private final ApiAuthConfig apiAuthConfig;

    @Override
    protected String doGetInterfaceSalt(String path) {
        return apiAuthConfig.getInterfaceSalt(path);
    }
}

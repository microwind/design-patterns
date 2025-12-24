package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 本地配置文件动态盐值生成策略
 * <p>
 * 从本地配置文件（apiauth-config.yml）读取接口固定盐值并生成动态盐值
 * <p>
 * 适用场景：小型项目，调用方较少（< 50个）
 */
@Component("localConfigDynamicSaltStrategy")
public class LocalConfigDynamicSaltStrategy extends AbstractDynamicSaltStrategy {
    private final ApiAuthConfig apiAuthConfig;

    public LocalConfigDynamicSaltStrategy(SignDomainService signDomainService,
                                          SignConfig signConfig,
                                          DynamicSaltMapper dynamicSaltMapper,
                                          ApiAuthConfig apiAuthConfig) {
        super(signDomainService, signConfig, dynamicSaltMapper);
        this.apiAuthConfig = apiAuthConfig;
    }

    @Override
    protected String getFixedSalt(String path) {
        String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，不支持获取动态盐值，路径：" + path);
        }
        return interfaceSalt;
    }

    @Override
    protected void checkPermissions(String appCode, String path) {
        // 检查动态盐值生成接口权限
        String dynamicSaltGeneratePath = signConfig.getDynamicSaltGeneratePath();
        if (apiAuthConfig.noPermission(appCode, dynamicSaltGeneratePath)) {
            throw new SecurityException(
                    String.format("应用 [%s] 无权访问动态盐值生成接口 [%s]", appCode, dynamicSaltGeneratePath)
            );
        }

        // 检查目标接口权限
        if (apiAuthConfig.noPermission(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }
    }
}

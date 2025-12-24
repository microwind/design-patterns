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
@RequiredArgsConstructor
public class LocalConfigDynamicSaltStrategy implements DynamicSaltGenerationStrategy {
    private final SignDomainService signDomainService;
    private final ApiAuthConfig apiAuthConfig;
    private final SignConfig signConfig;
    private final DynamicSaltMapper dynamicSaltMapper;

    @Override
    public DynamicSaltDTO generate(String appCode, String path) {
        // 获取接口盐值
        String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，不支持获取动态盐值，路径：" + path);
        }

        // 检查权限
        String dynamicSaltGeneratePath = signConfig.getDynamicSaltGeneratePath();
        if (apiAuthConfig.noPermission(appCode, dynamicSaltGeneratePath)) {
            throw new SecurityException(
                    String.format("应用 [%s] 无权访问动态盐值生成接口 [%s]", appCode, dynamicSaltGeneratePath)
            );
        }
        if (apiAuthConfig.noPermission(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 生成动态盐值
        Long saltTimestamp = System.currentTimeMillis();
        DynamicSalt dynamicSalt = signDomainService.generateDynamicSalt(appCode, path, interfaceSalt, saltTimestamp);

        return dynamicSaltMapper.toDTO(dynamicSalt);
    }
}

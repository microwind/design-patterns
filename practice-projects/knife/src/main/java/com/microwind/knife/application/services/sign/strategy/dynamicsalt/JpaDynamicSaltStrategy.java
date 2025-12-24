package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.application.services.apiauth.ApiAuthService;
import com.microwind.knife.application.services.apiauth.ApiDynamicSaltLogService;
import com.microwind.knife.application.services.apiauth.ApiInfoService;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA 数据库动态盐值生成策略
 * <p>
 * 通过 JPA 从数据库读取接口固定盐值并生成动态盐值
 * <p>
 * 适用场景：中大型项目，调用方较多（> 50个），使用 Spring Data JPA
 */
@Component("jpaDynamicSaltStrategy")
@RequiredArgsConstructor
public class JpaDynamicSaltStrategy implements DynamicSaltGenerationStrategy {
    private final SignDomainService signDomainService;
    private final SignConfig signConfig;
    private final ApiInfoService apiInfoService;
    private final ApiDynamicSaltLogService apiDynamicSaltLogService;
    private final ApiAuthService apiAuthService;
    private final DynamicSaltMapper dynamicSaltMapper;

    @Override
    public DynamicSaltDTO generate(String appCode, String path) {
        // 获取并验证接口信息和盐值
        ApiInfo apiInfo = getValidatedApiInfo(path);
        String interfaceSalt = apiInfo.getFixedSalt();
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }

        // 检查权限
        String dynamicSaltGeneratePath = signConfig.getDynamicSaltGeneratePath();
        if (!apiAuthService.checkAuth(appCode, dynamicSaltGeneratePath)) {
            throw new SecurityException(
                    String.format("应用 [%s] 无权访问动态盐值生成接口 [%s]", appCode, dynamicSaltGeneratePath)
            );
        }
        if (!apiAuthService.checkAuth(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 生成动态盐值
        Long saltTimestamp = System.currentTimeMillis();
        DynamicSalt dynamicSalt = signDomainService.generateDynamicSalt(appCode, path, interfaceSalt, saltTimestamp);

        // 根据配置决定是否保存到数据库
        if (signConfig.isValidateDynamicSaltFromDatabase()) {
            DynamicSaltDTO dto = dynamicSaltMapper.toDTO(dynamicSalt, apiInfo.getId());
            apiDynamicSaltLogService.save(dto);
        }

        return dynamicSaltMapper.toDTO(dynamicSalt);
    }

    /**
     * 获取并验证 API 信息
     * 确保接口存在且需要签名
     */
    private ApiInfo getValidatedApiInfo(String path) {
        ApiInfo apiInfo = apiInfoService.getByApiPath(path);
        if (apiInfo == null) {
            throw new IllegalArgumentException("API 信息不存在，路径：" + path);
        }

        ApiInfo.ApiType apiType = ApiInfo.ApiType.fromCode(apiInfo.getApiType());
        if (apiType != ApiInfo.ApiType.NEED_SIGN) {
            throw new IllegalArgumentException(
                    String.format("接口不需要签名验证，路径：%s，类型：%s", path, apiType.getDescription())
            );
        }

        return apiInfo;
    }
}

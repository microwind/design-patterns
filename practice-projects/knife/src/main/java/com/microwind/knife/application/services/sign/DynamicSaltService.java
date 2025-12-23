package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.ApiAuthConfig;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态盐值应用服务
 * <p>
 * 负责协调动态盐值的生成、验证和日志记录。
 * 支持两种配置模式：
 * 1. 本地文件配置方式（适合小型项目，调用方较少，小于50个）
 * 2. 数据库配置方式（适合中大型项目，调用方较多，大于50个）
 */
@Service
@RequiredArgsConstructor
public class DynamicSaltService {

    private final SignDomainService signDomainService;
    private final SignConfig signConfig;
    private final ApiAuthConfig apiAuthConfig;
    private final ApiInfoService apiInfoService;
    private final ApiDynamicSaltLogService apiDynamicSaltLogService;
    private final ApiAuthService apiAuthService;

    /**
     * 生成动态盐值
     *
     * @param appCode 调用方应用编码
     * @param path    接口路径
     * @return 生成的动态盐值 DTO 对象
     */
    @Transactional
    public DynamicSaltDTO generate(String appCode, String path) {
        String configMode = signConfig.getConfigMode();

        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            return generateFromDatabase(appCode, path);
        } else {
            return generateFromLocalConfig(appCode, path);
        }
    }

    /**
     * 校验动态盐值
     *
     * @param appCode         应用方
     * @param path            接口路径
     * @param dynamicSalt     动态盐值
     * @param dynamicSaltTime 动态盐值生成时间
     * @return 是否有效
     */
    public boolean validateDynamicSalt(String appCode, String path, String dynamicSalt, Long dynamicSaltTime) {
        // 参数校验
        if (appCode == null || path == null || dynamicSalt == null || dynamicSaltTime == null) {
            return false;
        }

        // 时间戳校验（防止时间回拨攻击）
        long now = System.currentTimeMillis();
        if (dynamicSaltTime > now) {
            throw new IllegalArgumentException("动态盐值时间不能大于当前时间：" + dynamicSaltTime);
        }

        String configMode = signConfig.getConfigMode();

        // 数据库模式 + 开启数据库校验：直接查库验证并消费
        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)
                && signConfig.isValidateDynamicSaltFromDatabase()) {
            return apiDynamicSaltLogService.validateAndConsumeSalt(appCode, path, dynamicSalt);
        }

        // 算法校验模式：检查 TTL 并重新计算
        Long ttl = signConfig.getDynamicSaltTtl();
        if ((now - dynamicSaltTime) > ttl) {
            throw new IllegalArgumentException("动态盐值已超过有效期：" + dynamicSaltTime);
        }

        String interfaceSalt = getInterfaceSalt(path, configMode);
        DynamicSaltDTO dynamicSaltDTO = new DynamicSaltDTO();
        dynamicSaltDTO.setAppCode(appCode);
        dynamicSaltDTO.setApiPath(path);
        dynamicSaltDTO.setDynamicSalt(dynamicSalt);
        dynamicSaltDTO.setSaltTimestamp(dynamicSaltTime);
        return signDomainService.validateDynamicSalt(dynamicSaltDTO, interfaceSalt);
    }

    /**
     * 从数据库配置生成动态盐值
     */
    private DynamicSaltDTO generateFromDatabase(String appCode, String path) {
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
            DynamicSaltDTO dto = DynamicSaltMapper.toDTO(dynamicSalt, apiInfo.getId());
            apiDynamicSaltLogService.save(dto);
        }

        return DynamicSaltMapper.toDTO(dynamicSalt);
    }

    /**
     * 从本地配置文件生成动态盐值
     */
    private DynamicSaltDTO generateFromLocalConfig(String appCode, String path) {
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

        return DynamicSaltMapper.toDTO(dynamicSalt);
    }

    /**
     * 获取接口盐值
     * 根据配置模式从数据库或本地配置获取
     */
    private String getInterfaceSalt(String path, String configMode) {
        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            // 从数据库获取
            ApiInfo apiInfo = getValidatedApiInfo(path);
            String interfaceSalt = apiInfo.getFixedSalt();
            if (interfaceSalt == null || interfaceSalt.isEmpty()) {
                throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
            }
            return interfaceSalt;
        } else {
            // 从本地配置获取
            String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
            if (interfaceSalt == null || interfaceSalt.isEmpty()) {
                throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
            }
            return interfaceSalt;
        }
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

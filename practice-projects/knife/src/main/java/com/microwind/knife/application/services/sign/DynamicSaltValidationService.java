package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.services.apiauth.ApiDynamicSaltLogService;
import com.microwind.knife.application.services.apiauth.ApiInfoService;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 动态盐值校验服务
 * <p>
 * 负责动态盐值的验证逻辑，支持两种校验模式：
 * <ul>
 *   <li>数据库校验模式：直接查库验证并消费（一次性使用）</li>
 *   <li>算法校验模式：根据配置的TTL和固定盐值重新计算验证</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class DynamicSaltValidationService {

    private final SignDomainService signDomainService;
    private final SignConfig signConfig;
    private final ApiAuthConfig apiAuthConfig;
    private final ApiInfoService apiInfoService;
    private final ApiDynamicSaltLogService apiDynamicSaltLogService;
    private final SignRepository signRepository;

    /**
     * 校验动态盐值
     *
     * @param appCode         应用编码
     * @param path            接口路径
     * @param dynamicSalt     动态盐值
     * @param dynamicSaltTime 动态盐值生成时间戳（毫秒）
     * @return true-校验通过，false-校验失败
     */
    public boolean validate(String appCode, String path, String dynamicSalt, Long dynamicSaltTime) {
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
            return validateFromDatabase(appCode, path, dynamicSalt);
        }

        // 算法校验模式：检查 TTL 并重新计算
        return validateByAlgorithm(appCode, path, dynamicSalt, dynamicSaltTime, configMode);
    }

    /**
     * 校验动态盐值（DTO版本）
     *
     * @param dto 动态盐值DTO
     * @return true-校验通过，false-校验失败
     */
    public boolean validate(DynamicSaltDTO dto) {
        return validate(dto.getAppCode(),
                dto.getApiPath(),
                dto.getDynamicSalt(),
                dto.getSaltTimestamp());
    }

    /**
     * 从数据库校验并消费动态盐值
     */
    private boolean validateFromDatabase(String appCode, String path, String dynamicSalt) {
        // 根据配置选择使用 SignRepository 或 JPA
        if (signConfig.isUseJdbcRepository()) {
            return signRepository.validateAndConsumeSalt(appCode, path, dynamicSalt);
        } else {
            return apiDynamicSaltLogService.validateAndConsumeSalt(appCode, path, dynamicSalt);
        }
    }

    /**
     * 通过算法校验动态盐值
     */
    private boolean validateByAlgorithm(String appCode, String path, String dynamicSalt,
                                        Long dynamicSaltTime, String configMode) {
        // 检查 TTL
        long now = System.currentTimeMillis();
        Long ttl = signConfig.getDynamicSaltTtl();
        if ((now - dynamicSaltTime) > ttl) {
            throw new IllegalArgumentException("动态盐值已超过有效期：" + dynamicSaltTime);
        }

        // 获取接口固定盐值
        String interfaceSalt = getInterfaceSalt(path, configMode);

        // 构建DTO并验证
        DynamicSaltDTO dynamicSaltDTO = new DynamicSaltDTO();
        dynamicSaltDTO.setAppCode(appCode);
        dynamicSaltDTO.setApiPath(path);
        dynamicSaltDTO.setDynamicSalt(dynamicSalt);
        dynamicSaltDTO.setSaltTimestamp(dynamicSaltTime);

        return signDomainService.validateDynamicSalt(dynamicSaltDTO, interfaceSalt);
    }

    /**
     * 获取接口盐值
     * 根据配置模式从数据库或本地配置获取
     */
    private String getInterfaceSalt(String path, String configMode) {
        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            return getInterfaceSaltFromDatabase(path);
        } else {
            return getInterfaceSaltFromLocalConfig(path);
        }
    }

    /**
     * 从数据库获取接口盐值
     */
    private String getInterfaceSaltFromDatabase(String path) {
        // 根据配置选择使用 SignRepository 或 JPA
        if (signConfig.isUseJdbcRepository()) {
            return getInterfaceSaltFromDatabaseViaJdbc(path);
        } else {
            return getInterfaceSaltFromDatabaseViaJpa(path);
        }
    }

    /**
     * 通过 JdbcTemplate (SignRepository) 从数据库获取接口盐值
     */
    private String getInterfaceSaltFromDatabaseViaJdbc(String path) {
        Optional<ApiInfo> apiInfoOpt = signRepository.findApiInfoByPath(path);
        if (apiInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("API 信息不存在，路径：" + path);
        }
        String interfaceSalt = apiInfoOpt.get().getFixedSalt();
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }
        return interfaceSalt;
    }

    /**
     * 通过 JPA 从数据库获取接口盐值
     */
    private String getInterfaceSaltFromDatabaseViaJpa(String path) {
        ApiInfo apiInfo = getValidatedApiInfo(path);
        String interfaceSalt = apiInfo.getFixedSalt();
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }
        return interfaceSalt;
    }

    /**
     * 从本地配置获取接口盐值
     */
    private String getInterfaceSaltFromLocalConfig(String path) {
        String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }
        return interfaceSalt;
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
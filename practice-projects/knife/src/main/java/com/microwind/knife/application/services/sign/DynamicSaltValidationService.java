package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.services.apiauth.ApiDynamicSaltLogService;
import com.microwind.knife.application.services.sign.strategy.interfacesalt.InterfaceSaltStrategyFactory;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final InterfaceSaltStrategyFactory interfaceSaltStrategyFactory;
    private final ValidationHelper validationHelper;
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
        // 1. 参数校验
        if (!validationHelper.validateParams(appCode, path, dynamicSalt) ||
                !validationHelper.validateParams(dynamicSaltTime)) {
            return false;
        }

        // 2. 时间戳校验
        validationHelper.validateTimestamp(dynamicSaltTime, "动态盐值");

        String configMode = signConfig.getConfigMode();

        // 3. 数据库模式 + 开启数据库校验：直接查库验证并消费
        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)
                && signConfig.isValidateDynamicSaltFromDatabase()) {
            return validateFromDatabase(appCode, path, dynamicSalt);
        }

        // 4. 算法校验模式
        return validateByAlgorithm(appCode, path, dynamicSalt, dynamicSaltTime);
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
                                        Long dynamicSaltTime) {
        // 检查 TTL
        validationHelper.validateTtl(dynamicSaltTime, signConfig.getDynamicSaltTtl(), "动态盐值");

        // 获取接口固定盐值（使用策略）
        String interfaceSalt = interfaceSaltStrategyFactory.getStrategy()
                .getInterfaceSalt(path);

        // 构建DTO并验证
        DynamicSaltDTO dynamicSaltDTO = new DynamicSaltDTO();
        dynamicSaltDTO.setAppCode(appCode);
        dynamicSaltDTO.setApiPath(path);
        dynamicSaltDTO.setDynamicSalt(dynamicSalt);
        dynamicSaltDTO.setSaltTimestamp(dynamicSaltTime);

        return signDomainService.validateDynamicSalt(dynamicSaltDTO, interfaceSalt);
    }
}

package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.sign.strategy.secretkey.SecretKeyStrategyFactory;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.domain.sign.SignDomainService;
import com.microwind.knife.domain.sign.SignUserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 签名应用服务
 * <p>
 * 负责协调签名的生成和校验。
 * 使用策略模式支持多种配置方式：
 * 1. 本地文件配置方式（适合小型项目，调用方较少，小于50个）
 * 2. JPA 数据库配置方式（适合中大型项目，使用 Spring Data JPA）
 * 3. JDBC 数据库配置方式（适合中大型项目，使用原生 JDBC）
 */
@Service
@RequiredArgsConstructor
public class SignService {
    private final SignDomainService signDomainService;
    private final SignRepository signRepository;
    private final DynamicSaltValidationService dynamicSaltValidationService;
    private final SignValidationService signValidationService;
    private final SecretKeyStrategyFactory secretKeyStrategyFactory;
    private final SignMapper signMapper;

    /**
     * 生成签名（不带参数）
     * <p>
     * 使用策略模式根据配置自动选择合适的秘钥获取方式
     *
     * @param signDTO 签名 DTO，包含 appCode、path、dynamicSalt、dynamicSaltTime
     * @return 签名 DTO
     */
    public SignDTO generate(SignDTO signDTO) {
        // 参数校验
        validateGenerateParams(signDTO);

        // 校验动态盐值
        validateDynamicSalt(signDTO);

        // 使用策略获取秘钥并生成签名（不带参数）
        String secretKey = secretKeyStrategyFactory.getStrategy()
                .getSecretKey(signDTO.getAppCode(), signDTO.getApiPath());
        Sign sign = signDomainService.generateSign(
                signDTO.getAppCode(),
                secretKey,
                signDTO.getApiPath()
        );
        return signMapper.toDTO(sign);
    }

    /**
     * 生成签名（带参数）
     * <p>
     * 使用策略模式根据配置自动选择合适的秘钥获取方式
     *
     * @param signDTO    签名 DTO，包含 appCode、path、dynamicSalt、dynamicSaltTime
     * @param params 请求参数
     * @return 签名 DTO
     */
    public SignDTO generateWithParams(SignDTO signDTO, Map<String, Object> params) {
        // 参数校验
        validateGenerateParams(signDTO);

        // 校验动态盐值
        validateDynamicSalt(signDTO);

        // 使用策略获取秘钥并生成签名（带参数）
        String secretKey = secretKeyStrategyFactory.getStrategy()
                .getSecretKey(signDTO.getAppCode(), signDTO.getApiPath());
        Sign sign = signDomainService.generateSignWithParams(
                signDTO.getAppCode(),
                secretKey,
                signDTO.getApiPath(),
                params
        );
        return signMapper.toDTO(sign);
    }

    /**
     * 校验生成签名的参数
     */
    private void validateGenerateParams(SignDTO signDTO) {
        if (signDTO.getAppCode() == null || signDTO.getApiPath() == null ||
                signDTO.getDynamicSalt() == null || signDTO.getDynamicSaltTime() == null) {
            throw new IllegalArgumentException("请求参数不完整。");
        }
    }

    /**
     * 校验动态盐值
     */
    private void validateDynamicSalt(SignDTO signDTO) {
        DynamicSaltDTO dynamicSaltDTO = new DynamicSaltDTO();
        dynamicSaltDTO.setAppCode(signDTO.getAppCode());
        dynamicSaltDTO.setApiPath(signDTO.getApiPath());
        dynamicSaltDTO.setDynamicSalt(signDTO.getDynamicSalt());
        dynamicSaltDTO.setSaltTimestamp(signDTO.getDynamicSaltTime());
        if (!dynamicSaltValidationService.validate(dynamicSaltDTO)) {
            throw new IllegalArgumentException("动态盐值校验失败");
        }
    }

    /**
     * 校验签名（不带参数）
     * <p>
     * 委托给 SignValidationService 处理
     *
     * @param signDTO 签名DTO
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSign(SignDTO signDTO) {
        return signValidationService.validate(signDTO);
    }

    /**
     * 校验签名（带参数）
     * <p>
     * 委托给 SignValidationService 处理
     *
     * @param signDTO    签名DTO
     * @param params 请求参数
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSignWithParams(SignDTO signDTO, Map<String, Object> params) {
        return signValidationService.validateWithParams(signDTO, params);
    }

    /**
     * 获取调用者全部 API 权限（聚合对象 SignUserAuth）
     *
     * @param appCode 调用方身份标识
     * @return SignUserAuth - 包含 secretKey、允许访问路径列表、禁止访问路径列表
     * @throws ResourceNotFoundException 如果找不到对应的 appCode，则抛出异常
     *
     * <p>说明：
     * 1. 直接返回聚合对象 SignUserAuth，而不是 Optional。
     */
    public SignUserAuth getSignUserAuth(String appCode) {
        return signRepository.findByAppCode(appCode)
                .orElseThrow(() -> new ResourceNotFoundException("没有找到对应AppCode。"));
    }
}

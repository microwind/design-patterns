package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.services.sign.strategy.secretkey.SecretKeyStrategyFactory;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 签名验证服务
 * <p>
 * 负责签名的校验逻辑，包括：
 * <ul>
 *   <li>时间戳校验</li>
 *   <li>签名有效期校验</li>
 *   <li>签名值校验</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignValidationService {

    private final SignDomainService signDomainService;
    private final SignConfig signConfig;
    private final SecretKeyStrategyFactory secretKeyStrategyFactory;
    private final ValidationHelper validationHelper;

    /**
     * 校验签名（不带参数）
     *
     * @param signDTO 签名DTO
     * @return true-校验通过，false-校验失败
     */
    public boolean validate(SignDTO signDTO) {
        // 1. 参数校验
        if (!validateParams(signDTO)) {
            return false;
        }

        // 2. 时间戳校验
        validationHelper.validateTimestamp(signDTO.getTimestamp(), "签名");

        // 3. TTL校验
        validationHelper.validateTtl(signDTO.getTimestamp(), signConfig.getSignatureTtl(), "签名");

        // 4. 获取秘钥并校验签名（不带参数）
        String secretKey = secretKeyStrategyFactory.getStrategy()
                .getSecretKey(signDTO.getAppCode(), signDTO.getApiPath());
        return signDomainService.validateSign(signDTO, secretKey);
    }

    /**
     * 校验签名（带参数）
     *
     * @param signDTO    签名DTO
     * @param params 请求参数
     * @return true-校验通过，false-校验失败
     */
    public boolean validateWithParams(SignDTO signDTO, Map<String, Object> params) {
        // 1. 参数校验
        if (!validateParams(signDTO)) {
            return false;
        }

        // 2. 时间戳校验
        validationHelper.validateTimestamp(signDTO.getTimestamp(), "签名");

        // 3. TTL校验
        validationHelper.validateTtl(signDTO.getTimestamp(), signConfig.getSignatureTtl(), "签名");

        // 4. 获取秘钥并校验签名（带参数）
        String secretKey = secretKeyStrategyFactory.getStrategy()
                .getSecretKey(signDTO.getAppCode(), signDTO.getApiPath());
        return signDomainService.validateSignWithParams(signDTO, secretKey, params);
    }

    /**
     * 校验必要参数
     *
     * @param signDTO 签名DTO
     * @return true-参数有效，false-参数无效
     */
    private boolean validateParams(SignDTO signDTO) {
        return validationHelper.validateParams(
                signDTO.getAppCode(),
                signDTO.getApiPath(),
                signDTO.getSignValue()
        ) && validationHelper.validateParams(signDTO.getTimestamp());
    }
}

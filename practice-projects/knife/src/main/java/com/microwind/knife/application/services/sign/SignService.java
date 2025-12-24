package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.sign.strategy.secretkey.SecretKeyStrategyFactory;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.domain.sign.SignDomainService;
import com.microwind.knife.domain.sign.SignUserAuth;
import com.microwind.knife.interfaces.vo.sign.SignVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

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
     * 生成签名
     * <p>
     * 使用策略模式根据配置自动选择合适的秘钥获取方式
     *
     * @param appCode       应用编码
     * @param path          接口路径
     * @param dynamicSalt   动态盐值
     * @param saltTimestamp 盐值时间戳
     * @return 签名 DTO
     */
    public SignDTO generate(String appCode, String path, String dynamicSalt, Long saltTimestamp) {
        // 参数校验
        if (appCode == null || path == null || dynamicSalt == null || saltTimestamp == null) {
            throw new IllegalArgumentException("请求参数不完整。");
        }

        // 校验动态盐值
        DynamicSaltDTO dynamicSaltDTO = new DynamicSaltDTO();
        dynamicSaltDTO.setAppCode(appCode);
        dynamicSaltDTO.setApiPath(path);
        dynamicSaltDTO.setDynamicSalt(dynamicSalt);
        dynamicSaltDTO.setSaltTimestamp(saltTimestamp);
        if (!dynamicSaltValidationService.validate(dynamicSaltDTO)) {
            throw new IllegalArgumentException("动态盐值校验失败");
        }

        // 使用策略获取秘钥并生成签名
        String secretKey = secretKeyStrategyFactory.getStrategy().getSecretKey(appCode, path);
        Sign sign = signDomainService.generateSign(appCode, secretKey, path);
        return signMapper.toDTO(sign);
    }

    public SignDTO generate(SignDTO signDTO) {
        return generate(signDTO.getAppCode(),
                signDTO.getApiPath(),
                signDTO.getDynamicSalt(),
                signDTO.getDynamicSaltTime());
    }

    /**
     * 校验签名
     * <p>
     * 委托给 SignValidationService 处理
     *
     * @param appCode  应用编码
     * @param path     接口路径
     * @param sign     签名值
     * @param signTime 签名时间戳（毫秒）
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSign(String appCode, String path, String sign, Long signTime) {
        return signValidationService.validate(appCode, path, sign, signTime);
    }

    /**
     * 校验签名（DTO版本）
     *
     * @param signDTO 签名DTO
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSign(SignDTO signDTO) {
        return signValidationService.validate(signDTO);
    }

    public boolean validateSign(SignVerifyRequest signVerifyRequest) {
        return signValidationService.validate(signMapper.toDTO(signVerifyRequest));
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

package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.apiauth.ApiUserDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.apiauth.ApiAuthService;
import com.microwind.knife.application.services.apiauth.ApiInfoService;
import com.microwind.knife.application.services.apiauth.ApiUsersService;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.apiauth.ApiUsers;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 签名应用服务
 * <p>
 * 负责协调签名的生成。
 * 支持两种配置模式：
 * 1. 本地文件配置方式（适合小型项目，调用方较少，小于50个）
 * 2. 数据库配置方式（适合中大型项目，调用方较多，大于50个）
 */
@Service
@RequiredArgsConstructor
public class SignService {
    private final SignDomainService signDomainService;
    private final DynamicSaltValidationService dynamicSaltValidationService;
    private final ApiAuthService apiAuthService;
    private final ApiUsersService apiUsersService;
    private final ApiInfoService apiInfoService;
    private final SignConfig signConfig;
    private final ApiAuthConfig apiAuthConfig;
    private final SignMapper signMapper;
    private final SignRepository signRepository;

    /**
     * 生成签名
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
            throw new IllegalArgumentException("请求参数不完整");
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

        // 获取秘钥并生成签名
        String secretKey = getSecretKey(appCode, path);
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
     * 获取应用秘钥
     * 根据配置模式从数据库或本地配置获取
     */
    private String getSecretKey(String appCode, String path) {
        String configMode = signConfig.getConfigMode();

        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            return getSecretKeyFromDatabase(appCode, path);
        } else {
            return getSecretKeyFromLocalConfig(appCode, path);
        }
    }

    /**
     * 从数据库获取秘钥
     */
    private String getSecretKeyFromDatabase(String appCode, String path) {
        // 根据配置选择使用 SignRepository 或 JPA
        if (signConfig.isUseJdbcRepository()) {
            return getSecretKeyFromDatabaseViaJdbc(appCode, path);
        } else {
            return getSecretKeyFromDatabaseViaJpa(appCode, path);
        }
    }

    /**
     * 通过 JdbcTemplate (SignRepository) 从数据库获取秘钥
     */
    private String getSecretKeyFromDatabaseViaJdbc(String appCode, String path) {
        // 验证接口信息
        Optional<ApiInfo> apiInfoOpt = signRepository.findApiInfoByPath(path);
        if (apiInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("API 信息不存在，路径：" + path);
        }
        ApiInfo apiInfo = apiInfoOpt.get();

        ApiInfo.ApiType apiType = ApiInfo.ApiType.fromCode(apiInfo.getApiType());
        if (apiType != ApiInfo.ApiType.NEED_SIGN) {
            throw new IllegalArgumentException(
                    String.format("接口不需要签名验证，路径：%s，类型：%s", path, apiType.getDescription())
            );
        }

        // 检查权限
        if (!signRepository.checkAuth(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 获取秘钥
        Optional<ApiUsers> apiUsersOpt = signRepository.findApiUserByAppCode(appCode);
        if (apiUsersOpt.isEmpty()) {
            throw new IllegalArgumentException("应用不存在，appCode：" + appCode);
        }

        return apiUsersOpt.get().getSecretKey();
    }

    /**
     * 通过 JPA 从数据库获取秘钥
     */
    private String getSecretKeyFromDatabaseViaJpa(String appCode, String path) {
        // 验证接口信息
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

        // 检查权限
        if (!apiAuthService.checkAuth(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 获取秘钥
        ApiUserDTO apiUserDTO = apiUsersService.getByAppCode(appCode);
        if (apiUserDTO.getAppCode() == null) {
            throw new IllegalArgumentException("应用不存在，appCode：" + appCode);
        }

        return apiUserDTO.getSecretKey();
    }

    /**
     * 从本地配置获取秘钥
     */
    private String getSecretKeyFromLocalConfig(String appCode, String path) {
        // 检查权限
        if (apiAuthConfig.noPermission(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 获取秘钥
        ApiAuthConfig.AppConfig appConfig = apiAuthConfig.getAppByKey(appCode);
        if (appConfig == null) {
            throw new IllegalArgumentException("应用不存在，appCode：" + appCode);
        }

        return appConfig.getAppSecret();
    }
}

package com.microwind.knife.application.services.sign.strategy.secretkey;

import com.microwind.knife.application.dto.apiauth.ApiUserDTO;
import com.microwind.knife.application.services.apiauth.ApiAuthService;
import com.microwind.knife.application.services.apiauth.ApiInfoService;
import com.microwind.knife.application.services.apiauth.ApiUsersService;
import com.microwind.knife.domain.apiauth.ApiInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA 数据库秘钥获取策略
 * <p>
 * 通过 JPA 从数据库读取应用秘钥
 * <p>
 * 适用场景：中大型项目，调用方较多（> 50个），使用 Spring Data JPA
 */
@Component("jpaSecretKeyStrategy")
@RequiredArgsConstructor
public class JpaSecretKeyStrategy implements SecretKeyRetrievalStrategy {
    private final ApiInfoService apiInfoService;
    private final ApiAuthService apiAuthService;
    private final ApiUsersService apiUsersService;

    @Override
    public String getSecretKey(String appCode, String path) {
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
}

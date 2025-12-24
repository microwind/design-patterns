package com.microwind.knife.application.services.sign.strategy.secretkey;

import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.apiauth.ApiUsers;
import com.microwind.knife.domain.repository.SignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JDBC 数据库秘钥获取策略
 * <p>
 * 通过 JdbcTemplate 从数据库读取应用秘钥
 * <p>
 * 适用场景：中大型项目，调用方较多（> 50个），使用原生 JDBC
 */
@Component("jdbcSecretKeyStrategy")
@RequiredArgsConstructor
public class JdbcSecretKeyStrategy implements SecretKeyRetrievalStrategy {
    private final SignRepository signRepository;

    @Override
    public String getSecretKey(String appCode, String path) {
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
}

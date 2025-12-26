package com.microwind.knife.application.services.sign.strategy.interfacesalt;

import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.repository.SignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * JDBC 数据库接口盐值获取策略
 * <p>
 * 通过 JdbcTemplate 从数据库读取接口固定盐值
 * <p>
 * 适用场景：中大型项目，调用方较多（> 50个），使用原生 JDBC
 */
@Component("jdbcInterfaceSaltStrategy")
@RequiredArgsConstructor
public class JdbcInterfaceSaltStrategy extends AbstractInterfaceSaltStrategy {
    private final SignRepository signRepository;

    @Override
    protected String doGetInterfaceSalt(String path) {
        ApiInfo apiInfo = getValidatedApiInfo(path);
        return apiInfo.getFixedSalt();
    }

    /**
     * 获取并验证 API 信息
     * 确保接口存在且需要签名
     */
    private ApiInfo getValidatedApiInfo(String path) {
        Optional<ApiInfo> apiInfoOpt = signRepository.findApiInfoByPath(path);
        if (apiInfoOpt.isEmpty()) {
            throw new IllegalArgumentException("API 信息不存在，路径：" + path);
        }
        ApiInfo apiInfo = apiInfoOpt.get();

        ApiInfo.ApiType apiType = ApiInfo.ApiType.fromCode(apiInfo.getApiType());
        if (apiType != ApiInfo.ApiType.NEED_SIGN) {
            throw new IllegalArgumentException(
                    String.format("接口不需要签名验证，路径：%s，类型：%s",
                            path, apiType.getDescription())
            );
        }

        return apiInfo;
    }
}

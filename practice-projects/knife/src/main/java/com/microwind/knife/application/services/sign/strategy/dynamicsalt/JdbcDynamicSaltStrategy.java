package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JDBC 数据库动态盐值生成策略
 * <p>
 * 通过 JdbcTemplate 从数据库读取接口固定盐值并生成动态盐值
 * <p>
 * 适用场景：中大型项目，调用方较多（> 50个），使用原生 JDBC
 */
@Component("jdbcDynamicSaltStrategy")
public class JdbcDynamicSaltStrategy extends AbstractDynamicSaltStrategy {
    private final SignRepository signRepository;

    public JdbcDynamicSaltStrategy(SignDomainService signDomainService,
                                   SignConfig signConfig,
                                   DynamicSaltMapper dynamicSaltMapper,
                                   SignRepository signRepository) {
        super(signDomainService, signConfig, dynamicSaltMapper);
        this.signRepository = signRepository;
    }

    @Override
    protected String getFixedSalt(String path) {
        ApiInfo apiInfo = getValidatedApiInfo(path);
        String interfaceSalt = apiInfo.getFixedSalt();
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }
        return interfaceSalt;
    }

    @Override
    protected void checkPermissions(String appCode, String path) {
        // 检查动态盐值生成接口权限
        String dynamicSaltGeneratePath = signConfig.getDynamicSaltGeneratePath();
        if (!signRepository.checkAuth(appCode, dynamicSaltGeneratePath)) {
            throw new SecurityException(
                    String.format("应用 [%s] 无权访问动态盐值生成接口 [%s]", appCode, dynamicSaltGeneratePath)
            );
        }

        // 检查目标接口权限
        if (!signRepository.checkAuth(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }
    }

    @Override
    protected void saveDynamicSaltLog(String appCode, String path,
                                      DynamicSalt dynamicSalt, Long saltTimestamp) {
        Optional<ApiInfo> apiInfoOpt = signRepository.findApiInfoByPath(path);
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(signConfig.getDynamicSaltTtl() / 1000);
        signRepository.saveDynamicSaltLog(
                appCode, apiInfoOpt.get().getId(), path, dynamicSalt.dynamicSalt(), saltTimestamp, expireTime
        );
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
                    String.format("接口不需要签名验证，路径：%s，类型：%s", path, apiType.getDescription())
            );
        }

        return apiInfo;
    }
}

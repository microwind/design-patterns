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
@RequiredArgsConstructor
public class JdbcDynamicSaltStrategy implements DynamicSaltGenerationStrategy {
    private final SignDomainService signDomainService;
    private final SignConfig signConfig;
    private final SignRepository signRepository;
    private final DynamicSaltMapper dynamicSaltMapper;

    @Override
    public DynamicSaltDTO generate(String appCode, String path) {
        // 获取并验证接口信息和盐值
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

        String interfaceSalt = apiInfo.getFixedSalt();
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }

        // 检查权限
        String dynamicSaltGeneratePath = signConfig.getDynamicSaltGeneratePath();
        if (!signRepository.checkAuth(appCode, dynamicSaltGeneratePath)) {
            throw new SecurityException(
                    String.format("应用 [%s] 无权访问动态盐值生成接口 [%s]", appCode, dynamicSaltGeneratePath)
            );
        }
        if (!signRepository.checkAuth(appCode, path)) {
            throw new SecurityException(String.format("应用 [%s] 无权访问目标接口 [%s]", appCode, path));
        }

        // 生成动态盐值
        Long saltTimestamp = System.currentTimeMillis();
        DynamicSalt dynamicSalt = signDomainService.generateDynamicSalt(appCode, path, interfaceSalt, saltTimestamp);

        // 根据配置决定是否保存到数据库
        if (signConfig.isValidateDynamicSaltFromDatabase()) {
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(signConfig.getDynamicSaltTtl() / 1000);
            signRepository.saveDynamicSaltLog(
                    appCode, apiInfo.getId(), path, dynamicSalt.dynamicSalt(), saltTimestamp, expireTime
            );
        }

        return dynamicSaltMapper.toDTO(dynamicSalt);
    }
}

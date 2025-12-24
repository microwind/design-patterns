package com.microwind.knife.application.services.sign.strategy.secretkey;

import com.microwind.knife.application.config.SignConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 秘钥获取策略工厂
 * <p>
 * 根据配置模式创建对应的秘钥获取策略实例
 * <p>
 * 支持的策略类型：
 * - local: 本地配置文件模式
 * - database (JPA): JPA 数据库模式
 * - database (JDBC): JDBC 数据库模式
 */
@Component
@RequiredArgsConstructor
public class SecretKeyStrategyFactory {
    private final SignConfig signConfig;
    private final Map<String, SecretKeyRetrievalStrategy> strategies;

    /**
     * 获取当前配置对应的策略实例
     *
     * @return 秘钥获取策略
     */
    public SecretKeyRetrievalStrategy getStrategy() {
        String configMode = signConfig.getConfigMode();

        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            // 数据库模式：根据 useJdbcRepository 选择 JPA 或 JDBC
            if (signConfig.isUseJdbcRepository()) {
                return getStrategyByName("jdbcSecretKeyStrategy");
            } else {
                return getStrategyByName("jpaSecretKeyStrategy");
            }
        } else {
            // 本地配置模式
            return getStrategyByName("localConfigSecretKeyStrategy");
        }
    }

    /**
     * 根据策略名称获取策略实例
     *
     * @param strategyName 策略Bean名称
     * @return 策略实例
     */
    private SecretKeyRetrievalStrategy getStrategyByName(String strategyName) {
        SecretKeyRetrievalStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalStateException("未找到秘钥获取策略：" + strategyName);
        }
        return strategy;
    }
}

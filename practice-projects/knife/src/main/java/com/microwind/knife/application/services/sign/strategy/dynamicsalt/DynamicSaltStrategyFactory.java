package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.config.SignConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 动态盐值生成策略工厂
 * <p>
 * 根据配置模式创建对应的动态盐值生成策略实例
 * <p>
 * 支持的策略类型：
 * - local: 本地配置文件模式
 * - database (JPA): JPA 数据库模式
 * - database (JDBC): JDBC 数据库模式
 */
@Component
@RequiredArgsConstructor
public class DynamicSaltStrategyFactory {
    private final SignConfig signConfig;
    private final Map<String, DynamicSaltGenerationStrategy> strategies;

    /**
     * 获取当前配置对应的策略实例
     *
     * @return 动态盐值生成策略
     */
    public DynamicSaltGenerationStrategy getStrategy() {
        String configMode = signConfig.getConfigMode();

        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            // 数据库模式：根据 useJdbcRepository 选择 JPA 或 JDBC
            if (signConfig.isUseJdbcRepository()) {
                return getStrategyByName("jdbcDynamicSaltStrategy");
            } else {
                return getStrategyByName("jpaDynamicSaltStrategy");
            }
        } else {
            // 本地配置模式
            return getStrategyByName("localConfigDynamicSaltStrategy");
        }
    }

    /**
     * 根据策略名称获取策略实例
     *
     * @param strategyName 策略Bean名称
     * @return 策略实例
     */
    private DynamicSaltGenerationStrategy getStrategyByName(String strategyName) {
        DynamicSaltGenerationStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalStateException("未找到动态盐值生成策略：" + strategyName);
        }
        return strategy;
    }
}

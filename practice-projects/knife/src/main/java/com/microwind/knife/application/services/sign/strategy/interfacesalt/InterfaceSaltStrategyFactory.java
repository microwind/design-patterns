package com.microwind.knife.application.services.sign.strategy.interfacesalt;

import com.microwind.knife.application.config.SignConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 接口固定盐值策略工厂
 * <p>
 * 根据配置自动选择合适的接口盐值获取策略：
 * <ul>
 *   <li>本地配置模式：使用 LocalConfigInterfaceSaltStrategy</li>
 *   <li>数据库模式 + JDBC：使用 JdbcInterfaceSaltStrategy</li>
 *   <li>数据库模式 + JPA：使用 JpaInterfaceSaltStrategy</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class InterfaceSaltStrategyFactory {

    private final SignConfig signConfig;
    private final Map<String, InterfaceSaltStrategy> strategies;

    /**
     * 获取当前配置对应的策略
     *
     * @return 接口盐值获取策略
     */
    public InterfaceSaltStrategy getStrategy() {
        String configMode = signConfig.getConfigMode();
        boolean useJdbc = signConfig.isUseJdbcRepository();

        if (SignConfig.CONFIG_MODE_DATABASE.equalsIgnoreCase(configMode)) {
            return useJdbc ?
                    strategies.get("jdbcInterfaceSaltStrategy") :
                    strategies.get("jpaInterfaceSaltStrategy");
        } else {
            return strategies.get("localConfigInterfaceSaltStrategy");
        }
    }
}

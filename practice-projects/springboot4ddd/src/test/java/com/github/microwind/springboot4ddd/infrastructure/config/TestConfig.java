package com.github.microwind.springboot4ddd.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 测试环境配置
 * 提供简化的测试专用配置
 *
 * @author jarry
 * @since 1.0.0
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * 测试环境的事务管理器
     */
    @Bean(name = "orderTransactionManager")
    @Primary
    public PlatformTransactionManager orderTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

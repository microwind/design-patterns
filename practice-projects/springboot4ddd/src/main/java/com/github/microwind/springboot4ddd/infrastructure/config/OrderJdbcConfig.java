package com.github.microwind.springboot4ddd.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

/**
 * Spring Data JDBC 配置
 * 配置 Order 使用 PostgreSQL 数据源
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableJdbcRepositories(
        basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.jdbc",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderJdbcConfig extends AbstractJdbcConfiguration {

    private final DataSource orderDataSource;

    @Value("${spring.order.fallback.enabled:true}")
    private boolean orderFallbackEnabled;

    public OrderJdbcConfig(@Qualifier("orderDataSource") DataSource orderDataSource) {
        this.orderDataSource = orderDataSource;
    }

    @Override
    public JdbcDialect jdbcDialect(NamedParameterJdbcOperations operations) {
        if (orderFallbackEnabled) {
            log.warn("订单数据库不可用或降级模式开启，跳过方言自动探测，使用 PostgreSQL 方言");
            return JdbcPostgresDialect.INSTANCE;
        }
        return super.jdbcDialect(operations);
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(orderDataSource);
    }
}

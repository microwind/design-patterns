package com.github.microwind.springboot4ddd.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Spring Data JDBC 配置
 * 配置 Order 使用 PostgreSQL 数据源
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
@EnableJdbcRepositories(
        basePackages = "com.github.microwind.springboot4ddd.infrastructure.repository.jdbc",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderJdbcConfig extends AbstractJdbcConfiguration {

    private final DataSource orderDataSource;

    public OrderJdbcConfig(@Qualifier("orderDataSource") DataSource orderDataSource) {
        this.orderDataSource = orderDataSource;
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(orderDataSource);
    }
}

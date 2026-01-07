package com.github.microwind.springboot4ddd.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 多数据源配置
 * 配置MySQL（用户数据）和PostgreSQL（订单数据）两个数据源
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
public class DataSourceConfig {

    /**
     * MySQL数据源配置 - 用户数据
     */
    @Bean(name = "userDataSource")
    @ConfigurationProperties(prefix = "spring.user.datasource")
    public DataSource userDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * PostgreSQL数据源配置 - 订单数据
     */
    @Bean(name = "orderDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.order.datasource")
    public DataSource orderDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    /**
     * MySQL JdbcTemplate - 用户数据
     */
    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * PostgreSQL JdbcTemplate - 订单数据
     */
    @Bean(name = "orderJdbcTemplate")
    @Primary
    public JdbcTemplate orderJdbcTemplate(@Qualifier("orderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * MySQL事务管理器 - 用户数据
     */
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(@Qualifier("userDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * PostgreSQL事务管理器 - 订单数据
     */
    @Bean(name = "orderTransactionManager")
    @Primary
    public PlatformTransactionManager orderTransactionManager(@Qualifier("orderDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

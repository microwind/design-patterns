package com.microwind.knife.infrastructure.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * User数据源配置 - 次数据源
 * 用于用户认证相关的数据库操作
 * 采用 JdbcTemplate 方式，不使用 JPA
 */
@Configuration
@EnableTransactionManagement
public class UserDataSourceConfig {

    @Value("${spring.user.datasource.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.user.datasource.username}")
    private String username;

    @Value("${spring.user.datasource.password}")
    private String password;

    @Value("${spring.user.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * User数据源 (次数据源)
     */
    @Bean(name = "userDataSource")
    public DataSource userDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        // 连接池配置
        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(10);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setAutoCommit(true);
        dataSource.setConnectionInitSql("SELECT 1");
        dataSource.setPoolName("UserDB-Pool");

        return dataSource;
    }

    /**
     * User数据源的 JdbcTemplate
     */
    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * User数据源的事务管理器（用于 JdbcTemplate）
     */
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(@Qualifier("userDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

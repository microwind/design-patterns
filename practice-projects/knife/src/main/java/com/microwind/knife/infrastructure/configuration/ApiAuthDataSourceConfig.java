package com.microwind.knife.infrastructure.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ApiAuth PostgreSQL数据源配置
 * 用于API验证相关的数据库操作
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.microwind.knife.domain.repository.apiauth", // API Sign Repository包路径
        entityManagerFactoryRef = "apiAuthEntityManagerFactory",
        transactionManagerRef = "apiAuthTransactionManager"
)
public class ApiAuthDataSourceConfig {

    @Value("${spring.apiauth.datasource.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.apiauth.datasource.username}")
    private String username;

    @Value("${spring.apiauth.datasource.password}")
    private String password;

    @Value("${spring.apiauth.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * API Sign数据源 (PostgreSQL)
     */
    @Bean(name = "apiAuthDataSource")
    public DataSource apiAuthDataSource() {
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
        dataSource.setPoolName("ApiAuthDB-Pool");

        return dataSource;
    }

    /**
     * API Sign数据源的EntityManagerFactory
     * 管理ApiAuth, ApiInfo, ApiUsers, ApiDynamicSaltLog实体
     */
    @Bean(name = "apiAuthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean apiAuthEntityManagerFactory(
            @Qualifier("apiAuthDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.microwind.knife.domain.apiauth"); // API Sign实体包路径
        em.setPersistenceUnitName("apiAuthPU");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * API Sign数据源的事务管理器
     */
    @Bean(name = "apiAuthTransactionManager")
    public PlatformTransactionManager apiAuthTransactionManager(
            @Qualifier("apiAuthEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * API Sign数据源的JdbcTemplate
     */
    @Bean(name = "apiAuthJdbcTemplate")
    public JdbcTemplate apiAuthJdbcTemplate(@Qualifier("apiAuthDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

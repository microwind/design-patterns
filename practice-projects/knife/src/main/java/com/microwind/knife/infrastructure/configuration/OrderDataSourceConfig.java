package com.microwind.knife.infrastructure.configuration;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
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
 * Order数据源配置 - 主数据源
 * 用于订单相关的数据库操作
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
            "com.microwind.knife.domain.repository.order",
//            "com.microwind.knife.infrastructure.repository"
        },
        // excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.microwind\\.knife\\.domain\\.repository\\.apiauth\\..*"),
        entityManagerFactoryRef = "orderEntityManagerFactory",
        transactionManagerRef = "orderTransactionManager"
)
public class OrderDataSourceConfig {

    @Value("${spring.order.datasource.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.order.datasource.username}")
    private String username;

    @Value("${spring.order.datasource.password}")
    private String password;

    @Value("${spring.order.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * Order数据源 (主数据源)
     */
    @Bean(name = "orderDataSource")
    @Primary
    public DataSource orderDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        // 连接池配置
        dataSource.setMinimumIdle(5);
        dataSource.setMaximumPoolSize(20);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        dataSource.setAutoCommit(true);
        dataSource.setConnectionInitSql("SELECT 1");
        dataSource.setPoolName("OrderDB-Pool");

        return dataSource;
    }

    /**
     * Order数据源的EntityManagerFactory
     * 管理Order和OrderItem实体
     */
    @Bean(name = "orderEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(
            @Qualifier("orderDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.microwind.knife.domain.order"); // Order实体包路径
        em.setPersistenceUnitName("orderPU");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate"); // 改回validate，因为已经明确指定列名
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * Order数据源的事务管理器
     */
    @Bean(name = "orderTransactionManager")
    @Primary
    public PlatformTransactionManager orderTransactionManager(
            @Qualifier("orderEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * Order数据源的JdbcTemplate (主JdbcTemplate)
     */
    @Bean(name = "orderJdbcTemplate")
    @Primary
    public JdbcTemplate orderJdbcTemplate(@Qualifier("orderDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

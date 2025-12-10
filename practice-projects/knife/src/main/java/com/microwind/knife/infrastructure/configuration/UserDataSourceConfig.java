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
 * User数据源配置 - 次数据源
 * 用于用户认证相关的数据库操作
 *
 * 注意：如果frog数据库暂时不可用，可以注释掉@Configuration来禁用此配置
 */
@Configuration
@EnableTransactionManagement
// 采用JdbcTemplate则可以不用JPA
//@EnableJpaRepositories(
//        basePackages = "com.microwind.knife.domain.repository", // User JPA Repository实体包路径
//        entityManagerFactoryRef = "userEntityManagerFactory",
//        transactionManagerRef = "userTransactionManager"
//)
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

//  User没有采用JPA，因此可以注释掉，而是直接采用JdbcTemplate
//    /**
//     * User数据源的EntityManagerFactory
//     * 管理User实体
//     */
//    @Bean(name = "userEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(
//            @Qualifier("userDataSource") DataSource dataSource) {
//
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.microwind.knife.domain.user"); // User实体包路径
//        em.setPersistenceUnitName("userPU");
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.hbm2ddl.auto", "validate"); // 改回validate，因为已经明确指定列名
//        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        properties.put("hibernate.show_sql", true);
//        properties.put("hibernate.format_sql", true);
//        em.setJpaPropertyMap(properties);
//
//        return em;
//    }
//
//    /**
//     * User数据源的事务管理器
//     */
//    @Bean(name = "userTransactionManager")
//    public PlatformTransactionManager userTransactionManager(
//            @Qualifier("userEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }

    /**
     * User数据源的JdbcTemplate
     */
    @Bean(name = "userJdbcTemplate")
    public JdbcTemplate userJdbcTemplate(@Qualifier("userDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}

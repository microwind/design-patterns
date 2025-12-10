package com.microwind.knife.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据库配置展示类
 * 用于启动时展示双数据源配置信息
 *
 * 配合 OrderDataSourceConfig 和 UserDataSourceConfig 使用
 * 在应用启动时打印数据源的配置信息，便于调试和确认配置
 *
 * 注意：如果User数据源未启用，需要注释掉这个类
 */
@Configuration
public class DatabaseConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    // Order数据源配置
    @Value("${spring.order.datasource.jdbc-url}")
    private String orderUrl;

    @Value("${spring.order.datasource.username}")
    private String orderUsername;

    @Value("${spring.order.datasource.password}")
    private String orderPassword;

    // User数据源配置
    @Value("${spring.user.datasource.jdbc-url}")
    private String userUrl;

    @Value("${spring.user.datasource.username}")
    private String userUsername;

    @Value("${spring.user.datasource.password}")
    private String userPassword;

    private final DataSource orderDataSource;
    private final DataSource userDataSource;

    public DatabaseConfig(
            @Qualifier("orderDataSource") DataSource orderDataSource,
            @Qualifier("userDataSource") DataSource userDataSource) {
        this.orderDataSource = orderDataSource;
        this.userDataSource = userDataSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("========== Order Database (Primary) ==========");
        logger.info("Database URL: {}", orderUrl);
        logger.info("Database Username: {}", orderUsername);
        logger.info("Database Password: {}", maskPassword(orderPassword));
        logger.info("DataSource Type: {}", orderDataSource.getClass().getSimpleName());

        logger.info("========== User Database (Secondary) ==========");
        logger.info("Database URL: {}", userUrl);
        logger.info("Database Username: {}", userUsername);
        logger.info("Database Password: {}", maskPassword(userPassword));
        logger.info("DataSource Type: {}", userDataSource.getClass().getSimpleName());
        logger.info("===============================================");
    }

    /**
     * 对密码进行脱敏处理
     */
    private String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return "***";
        }
        return password.substring(0, 1) + "***" + password.substring(password.length() - 1);
    }
}
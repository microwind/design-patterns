package com.github.microwind.userdemo.config;

import com.github.microwind.springwind.annotation.Bean;
import com.github.microwind.springwind.annotation.Configuration;
import com.github.microwind.springwind.jdbc.JdbcTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * 数据库配置类
 * 使用 HikariCP 连接池配置数据源
 * 配置从 application.yml 文件读取
 */
@Configuration
public class DataSourceConfig {

    /**
     * 创建数据源
     */
    @Bean
    public DataSource dataSource() {
        // 使用 YamlConfigLoader 读取配置
        YamlConfigLoader config = YamlConfigLoader.getInstance();
        
        HikariConfig hikariConfig = new HikariConfig();
        
        // 从配置文件读取数据库连接信息，如果没有则使用默认值
        String jdbcUrl = config.getString("user.datasource.jdbc-url", 
                "jdbc:mysql://localhost:3306/frog?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC");
        String username = config.getString("user.datasource.username", "frog_admin");
        String password = config.getString("user.datasource.password", "frog798");
        String driverClassName = config.getString("user.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
        
        // 连接池配置
        int maxPoolSize = config.getInt("user.datasource.maximum-pool-size", 10);
        int minIdle = config.getInt("user.datasource.minimum-idle", 5);
        long connTimeout = config.getLong("user.datasource.connection-timeout", 30000);
        long idleTimeout = config.getLong("user.datasource.idle-timeout", 600000);
        long maxLifetime = config.getLong("user.datasource.max-lifetime", 1800000);
        
        // 设置数据库连接信息
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);
        
        // 设置连接池参数
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minIdle);
        hikariConfig.setConnectionTimeout(connTimeout);    // 单位：毫秒
        hikariConfig.setIdleTimeout(idleTimeout);         // 单位：毫秒
        hikariConfig.setMaxLifetime(maxLifetime);         // 单位：毫秒
        hikariConfig.setAutoCommit(true);
        
        // 池名称
        hikariConfig.setPoolName("UserDemoPool");
        
        // 打印数据源配置信息
        printDataSourceConfig(jdbcUrl, maxPoolSize, minIdle, connTimeout);
        
        return new HikariDataSource(hikariConfig);
    }

    /**
     * 创建 JdbcTemplate
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 打印数据源配置信息
     */
    private void printDataSourceConfig(String jdbcUrl, int maxPoolSize, int minIdle, long connTimeout) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("数据源配置信息");
        System.out.println("=".repeat(60));
        System.out.println("JDBC URL: " + jdbcUrl);
        System.out.println("最大连接数: " + maxPoolSize);
        System.out.println("最小空闲连接: " + minIdle);
        System.out.println("连接超时: " + connTimeout + "ms");
        System.out.println("=".repeat(60) + "\n");
    }
}


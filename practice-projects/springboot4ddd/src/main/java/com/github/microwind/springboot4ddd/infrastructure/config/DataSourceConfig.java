package com.github.microwind.springboot4ddd.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

/**
 * 多数据源配置
 * 配置MySQL（用户数据）和PostgreSQL（订单数据）两个数据源
 * 支持优雅降级：当数据库连接失败时，系统仍能正常启动
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${spring.user.fallback.enabled:true}")
    private boolean userFallbackEnabled;

    @Value("${spring.order.fallback.enabled:true}")
    private boolean orderFallbackEnabled;

    /**
     * MySQL数据源配置 - 用户数据
     * 支持优雅降级：当连接失败时记录错误日志但不阻止应用启动
     */
    @Bean(name = "userHikariConfig")
    @ConfigurationProperties(prefix = "spring.user.datasource")
    public HikariConfig userHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "userDataSource")
    public DataSource userDataSource(@Qualifier("userHikariConfig") HikariConfig hikariConfig) {
        try {
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            
            // 测试连接以验证数据库可用性
            testConnection(dataSource, "MySQL(User)");
            log.info("MySQL用户数据源初始化成功");
            return dataSource;
            
        } catch (Exception e) {
            if (userFallbackEnabled) {
                log.error("MySQL用户数据源初始化失败，系统将继续运行但相关功能可能不可用: {}", e.getMessage());
                log.warn("建议检查数据库连接配置和服务状态");
                
                // 创建一个虚拟数据源，避免启动失败
                return createDummyDataSource("MySQL(User)");
            } else {
                log.error("MySQL用户数据源初始化失败，优雅降级已禁用: {}", e.getMessage());
                throw new RuntimeException("MySQL用户数据源初始化失败", e);
            }
        }
    }

    /**
     * PostgreSQL数据源配置 - 订单数据
     * 支持优雅降级：当连接失败时记录错误日志但不阻止应用启动
     */
    @Bean(name = "orderHikariConfig")
    @ConfigurationProperties(prefix = "spring.order.datasource")
    public HikariConfig orderHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "orderDataSource")
    @Primary
    public DataSource orderDataSource(@Qualifier("orderHikariConfig") HikariConfig hikariConfig) {
        try {
            HikariDataSource dataSource = new HikariDataSource(hikariConfig);
            
            // 测试连接以验证数据库可用性
            testConnection(dataSource, "PostgreSQL(Order)");
            log.info("PostgreSQL订单数据源初始化成功");
            return dataSource;
            
        } catch (Exception e) {
            if (orderFallbackEnabled) {
                log.error("PostgreSQL订单数据源初始化失败，系统将继续运行但相关功能可能不可用: {}", e.getMessage());
                log.warn("建议检查数据库连接配置和服务状态");
                
                // 创建一个虚拟数据源，避免启动失败
                return createDummyDataSource("PostgreSQL(Order)");
            } else {
                log.error("PostgreSQL订单数据源初始化失败，优雅降级已禁用: {}", e.getMessage());
                throw new RuntimeException("PostgreSQL订单数据源初始化失败", e);
            }
        }
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

    /**
     * 测试数据库连接
     */
    private void testConnection(DataSource dataSource, String dbName) {
        try (var connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                log.info("{} 数据库连接测试成功", dbName);
            } else {
                throw new RuntimeException(dbName + " 数据库连接无效");
            }
        } catch (Exception e) {
            throw new RuntimeException(dbName + " 数据库连接测试失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建虚拟数据源，用于优雅降级
     * 当真实数据库不可用时，返回一个不会导致应用启动失败的数据源
     */
    private DataSource createDummyDataSource(String dbName) {
        log.warn("为 {} 创建虚拟数据源，相关数据库操作将失败", dbName);
        
        // 返回一个始终抛出异常的数据源
        return new DataSource() {
            @Override
            public java.sql.Connection getConnection() throws java.sql.SQLException {
                throw new java.sql.SQLException(dbName + " 数据库不可用，请检查数据库服务状态");
            }
            
            @Override
            public java.sql.Connection getConnection(String username, String password) throws java.sql.SQLException {
                throw new java.sql.SQLException(dbName + " 数据库不可用，请检查数据库服务状态");
            }
            
            @Override
            public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
                throw new java.sql.SQLException(dbName + " 虚拟数据源不支持 unwrap 操作");
            }
            
            @Override
            public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
                return false;
            }
            
            // 其他必需方法的空实现
            @Override public java.io.PrintWriter getLogWriter() throws java.sql.SQLException { return null; }
            @Override public void setLogWriter(java.io.PrintWriter out) throws java.sql.SQLException {}
            @Override public void setLoginTimeout(int seconds) throws java.sql.SQLException {}
            @Override public int getLoginTimeout() throws java.sql.SQLException { return 0; }
            @Override public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException { return null; }
        };
    }
}

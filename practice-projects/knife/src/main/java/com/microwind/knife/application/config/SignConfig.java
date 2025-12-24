package com.microwind.knife.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 签名配置类
 * 用于配置签名相关的参数，包括动态盐值有效期、签名有效期等
 */
@Configuration
@ConfigurationProperties(prefix = "sign")
@Data
public class SignConfig {

    private static final long DEFAULT_DYNAMIC_SALT_TTL = 86400000L; // 24h
    private static final long DEFAULT_SIGNATURE_TTL = 600000L;      // 10min

    public static final String CONFIG_MODE_DATABASE = "database";
    public static final String CONFIG_MODE_FILE = "file";
    
    public static final String REPOSITORY_TYPE_JDBC = "jdbc";
    public static final String REPOSITORY_TYPE_JPA = "jpa";

    /**
     * 动态盐值配置
     */
    private DynamicSaltConfig dynamicSalt = new DynamicSaltConfig();

    /**
     * 签名配置
     */
    private SignatureConfig signature = new SignatureConfig();

    /**
     * 配置模式：database 或 file
     * - database: 通过数据库配置（适合大规模应用）
     * - file: 通过本地配置文件（适合小规模应用）
     */
    private String configMode = CONFIG_MODE_DATABASE;

    /**
     * 仓库类型：jdbc 或 jpa
     * - jdbc: 使用 SignRepository（JdbcTemplate 实现）
     * - jpa: 使用 JPA Repository
     */
    private String repositoryType = REPOSITORY_TYPE_JDBC;

    /**
     * 动态盐值生成接口路径，来自application.yml配置
     */
    private String dynamicSaltGeneratePath;

    /**
     * 签名生成接口路径，来自application.yml配置
     */
    private String signGeneratePath;

    /**
     * 动态盐值配置
     */
    @Data
    public static class DynamicSaltConfig {
        /**
         * 动态盐值有效期（毫秒）
         */
        private Long ttl = DEFAULT_DYNAMIC_SALT_TTL;

        /**
         * 是否使用数据库校验动态盐值
         * true: 从数据库查询动态盐值记录进行校验
         * false: 仅通过算法校验动态盐值
         * 默认：false
         */
        private Boolean validateFromDatabase = false;
    }

    /**
     * 签名配置
     */
    @Data
    public static class SignatureConfig {
        /**
         * 签名有效期（毫秒）
         */
        private Long ttl = DEFAULT_SIGNATURE_TTL;
    }

    /**
     * 获取动态盐值有效期（毫秒）
     */
    public Long getDynamicSaltTtl() {
        return dynamicSalt != null ? dynamicSalt.getTtl() : DEFAULT_DYNAMIC_SALT_TTL;
    }

    /**
     * 获取签名有效期（毫秒）
     */
    public Long getSignatureTtl() {
        return signature != null ? signature.getTtl() : DEFAULT_SIGNATURE_TTL;
    }

    /**
     * 是否使用数据库校验动态盐值
     */
    public Boolean isValidateDynamicSaltFromDatabase() {
        return dynamicSalt != null && Boolean.TRUE.equals(dynamicSalt.getValidateFromDatabase());
    }

    /**
     * 是否使用 JdbcTemplate 方式（SignRepository）
     */
    public boolean isUseJdbcRepository() {
        return REPOSITORY_TYPE_JDBC.equalsIgnoreCase(repositoryType);
    }

    /**
     * 是否使用 JPA 方式
     */
    public boolean isUseJpaRepository() {
        return REPOSITORY_TYPE_JPA.equalsIgnoreCase(repositoryType);
    }
}



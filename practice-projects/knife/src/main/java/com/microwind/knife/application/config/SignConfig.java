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

    public static final String HEADER_APP_CODE = "Sign-appCode";
    public static final String HEADER_SIGN = "Sign-sign";
    public static final String HEADER_TIME = "Sign-time";
    public static final String HEADER_PATH = "Sign-path";
    public static final String HEADER_DYNAMIC_SALT = "Sign-dynamicSalt";
    public static final String HEADER_DYNAMIC_SALT_TIME = "Sign-dynamicSaltTime";
    public static final String HEADER_WITH_PARAMS = "Sign-withParams";

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
    public String dynamicSaltGeneratePath;

    /**
     * 签名生成接口路径，来自application.yml配置
     */
    public String signGeneratePath;

    /**
     * 需要缓存请求体的路径模式列表
     * <p>
     * 只有这些路径的请求才会被 CachedBodyFilter 缓存 body
     * 支持 Ant 风格路径模式：
     * - /api/payment/** : 匹配 /api/payment 下所有路径
     * - /api/*\/sensitive : 匹配 /api/任意单层/sensitive
     * - 留空则使用 header 检测模式（默认）
     */
    public java.util.List<String> cachedBodyPathPatterns;

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

        /**
         * 默认是否使用参数签名
         * true: 签名计算包含请求参数（使用 SM3 算法）
         * false: 签名计算不包含请求参数（使用 SHA-256 算法）
         * 默认：false
         */
        private Boolean defaultWithParams = false;
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

    /**
     * 获取默认的 withParams 配置
     */
    public boolean isDefaultWithParams() {
        return signature != null && Boolean.TRUE.equals(signature.getDefaultWithParams());
    }
}



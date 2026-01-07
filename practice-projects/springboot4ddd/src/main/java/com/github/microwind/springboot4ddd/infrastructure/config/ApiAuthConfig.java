package com.github.microwind.springboot4ddd.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * API认证配置类
 * 从 apiauth-config.yml 文件读取签名配置
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "apiauth")
@Data
public class ApiAuthConfig {

    /**
     * 调用方应用配置列表
     */
    private List<AppConfig> apps = new ArrayList<>();

    /**
     * 接口盐值配置列表
     */
    private List<InterfaceSalt> interfaceSalts = new ArrayList<>();

    /**
     * 调用方配置
     */
    @Data
    public static class AppConfig {
        /**
         * 应用编码
         */
        private String appCode;

        /**
         * 应用名称
         */
        private String appName;

        /**
         * 密钥
         */
        private String secretKey;

        /**
         * 描述信息
         */
        private String description;

        /**
         * 权限路径列表
         */
        private List<String> permissions = new ArrayList<>();
    }

    /**
     * 接口盐值配置
     */
    @Data
    public static class InterfaceSalt {
        /**
         * 接口路径
         */
        private String path;

        /**
         * 盐值
         */
        private String salt;
    }

    /**
     * 根据appCode获取应用配置
     *
     * @param appCode 应用编码
     * @return 应用配置，不存在返回null
     */
    public AppConfig getAppConfigByCode(String appCode) {
        if (appCode == null || apps == null) {
            return null;
        }
        return apps.stream()
                .filter(app -> appCode.equals(app.getAppCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据路径获取接口盐值
     *
     * @param path 接口路径
     * @return 盐值，不存在返回null
     */
    public String getSaltByPath(String path) {
        if (path == null || interfaceSalts == null) {
            return null;
        }
        return interfaceSalts.stream()
                .filter(salt -> matchPath(salt.getPath(), path))
                .findFirst()
                .map(InterfaceSalt::getSalt)
                .orElse(null);
    }

    /**
     * 检查应用是否有权限访问指定路径
     *
     * @param appCode 应用编码
     * @param path    请求路径
     * @return true有权限，false无权限
     */
    public boolean hasPermission(String appCode, String path) {
        AppConfig appConfig = getAppConfigByCode(appCode);
        if (appConfig == null || appConfig.getPermissions() == null) {
            return false;
        }

        // 检查是否有匹配的权限
        return appConfig.getPermissions().stream()
                .anyMatch(permission -> matchPath(permission, path));
    }

    /**
     * 路径匹配
     * 支持路径模板匹配，如 /api/users/{userId} 可以匹配 /api/users/123
     *
     * @param pattern 模式路径（可能包含{xxx}占位符）
     * @param path    实际路径
     * @return true匹配，false不匹配
     */
    private boolean matchPath(String pattern, String path) {
        if (pattern == null || path == null) {
            return false;
        }

        // 完全匹配
        if (pattern.equals(path)) {
            return true;
        }

        // 支持路径模板匹配（简化版本）
        // 将 /api/users/{userId} 转换为正则表达式 /api/users/[^/]+
        String regex = pattern.replaceAll("\\{[^/]+\\}", "[^/]+");
        return path.matches(regex);
    }
}

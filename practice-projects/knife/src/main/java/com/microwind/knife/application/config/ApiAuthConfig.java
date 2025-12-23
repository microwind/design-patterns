package com.microwind.knife.application.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.AntPathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * 通过配置文件来鉴权，此种方式只在很少调用方（如20个以内）中使用
 * 实际项目一般采取数据库的方式来实现鉴权，如20个以上调用方
 */
@Data
@Configuration
@PropertySource(value = "classpath:apiauth-config.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "apiauth")
public class ApiAuthConfig {

    private List<AppConfig> apps;

    // 属性名要匹配 YAML 中的 key（kebab-case 转 camelCase）
    private List<InterfaceSalt> interfaceSalts;
    private ValidationConfig validationConfig;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Data
    public static class InterfaceSalt {
        private String path;
        private String salt;
    }

    // 内部缓存的 Map
    private volatile Map<String, String> interfaceSaltMap;

    // 启动时初始化，不需要懒加载
//    @PostConstruct
//    public void init() {
//        if (interfaceSalts != null && !interfaceSalts.isEmpty()) {
//            interfaceSaltMap = interfaceSalts.stream()
//                    .filter(salt -> salt.getPath() != null && salt.getSalt() != null)
//                    .collect(Collectors.toMap(
//                            InterfaceSalt::getPath,
//                            InterfaceSalt::getSalt,
//                            (v1, v2) -> v1
//                    ));
//        } else {
//            interfaceSaltMap = Collections.emptyMap();
//        }
//    }

    public Map<String, String> getInterfaceSaltMap() {
        // 第一次检查（无锁）
        if (interfaceSaltMap == null) {
            synchronized (this) {
                // 第二次检查（有锁）
                if (interfaceSaltMap == null) {
                    if (interfaceSalts != null && !interfaceSalts.isEmpty()) {
                        interfaceSaltMap = interfaceSalts.stream()
                                .filter(salt -> salt.getPath() != null && salt.getSalt() != null)
                                .collect(Collectors.toMap(
                                        InterfaceSalt::getPath,
                                        InterfaceSalt::getSalt,
                                        (v1, v2) -> v1 // 处理重复 key
                                ));
                    } else {
                        interfaceSaltMap = Collections.emptyMap();
                    }
                }
            }
        }
        return interfaceSaltMap;
    }

    @Data
    public static class AppConfig {
        private String appCode;
        private String appSecret;
        private String description;
        private List<String> permissions;
    }

    @Data
    public static class ValidationConfig {
        private Long dynamicSaltTtl;
        private Long signTtl;
    }

    /**
     * 根据appCode获取应用配置
     */
    public AppConfig getAppByKey(String appCode) {
        if (apps == null) {
            return null;
        }
        return apps.stream()
                .filter(app -> app.getAppCode().equals(appCode))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据接口路径获取接口盐值
     */
    public String getInterfaceSalt(String path) {
        if (path == null) {
            return null;
        }
        return interfaceSaltMap.get(path);
    }

    public List<String> getPermissionByAppCode(String appCode) {
        AppConfig app = getAppByKey(appCode);
        if (app == null || app.getPermissions() == null) {
            return null;
        }
        return app.getPermissions();
    }

    /**
     * 检查应用是否有权限访问指定接口
     * 支持精确匹配和路径变量匹配
     *
     * @param appCode 应用编码
     * @param path 请求路径，如 /api/users/123
     * @return true 表示有权限
     */
    public boolean hasPermission(String appCode, String path) {
        AppConfig app = getAppByKey(appCode);
        if (app == null || app.getPermissions() == null || app.getPermissions().isEmpty()) {
            return false;
        }

        // 1. 先尝试精确匹配（性能最好）
        if (app.getPermissions().contains(path)) {
            return true;
        }

        // 2. 再尝试路径模式匹配
        return app.getPermissions().stream()
                .anyMatch(permission -> pathMatcher.match(permission, path));
    }

    public boolean noPermission(String appCode, String path) {
        return !hasPermission(appCode, path);
    }
}

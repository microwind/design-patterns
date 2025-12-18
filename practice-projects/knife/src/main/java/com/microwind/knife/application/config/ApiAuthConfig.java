package com.microwind.knife.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/*
* 通过配置文件来鉴权，此种方式废弃
*/
@Data
@Configuration
@PropertySource(value = "classpath:apiauth-config.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "apiauth")
public class ApiAuthConfig {

    private List<AppConfig> apps;
    private Map<String, String> interfaceSalts;
    private ValidationConfig validation;

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
        if (interfaceSalts == null) {
            return null;
        }
        path = path.replaceAll("/", "");
        return interfaceSalts.get(path);
    }

    /**
     * 检查应用是否有权限访问指定接口
     */
    public boolean hasPermission(String appCode, String path) {
        AppConfig app = getAppByKey(appCode);
        if (app == null || app.getPermissions() == null) {
            return false;
        }
        return app.getPermissions().contains(path);
    }
}

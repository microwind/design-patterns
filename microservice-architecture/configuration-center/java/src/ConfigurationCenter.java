package src;

import java.util.HashMap;
import java.util.Map;

/**
 * ConfigurationCenter - 配置中心模式的 Java 实现
 *
 * 【设计模式】
 *   - 观察者模式（Observer Pattern）：实际工程中配置变更会推送通知到客户端，
 *     本示例简化为客户端主动 refresh。
 *   - 单例模式（Singleton Pattern）：ConfigCenter 通常全局唯一。
 *   - 代理模式（Proxy Pattern）：ConfigClient 代理 ConfigCenter 访问并缓存配置。
 *
 * 【架构思想】
 *   配置中心将所有服务配置集中存储，按"服务名+环境"维度管理，
 *   支持客户端运行时刷新，无需重新部署。
 *
 * 【开源对比】
 *   - Apollo（携程）：MySQL 存储 + 长轮询推送 + 灰度发布
 *   - Nacos（阿里）：支持配置管理和服务发现，长连接 push 通知
 *   - Spring Cloud Config：Git 仓库存储 + Webhook 刷新
 *   本示例用内存 Map 简化，省略了持久化、推送和权限控制。
 */
public class ConfigurationCenter {

    /**
     * ServiceConfig - 服务配置（值对象）
     *
     * 包含服务标识（serviceName + environment）和具体配置项。
     * version 字段用于跟踪配置版本，支持变更检测。
     */
    public static class ServiceConfig {
        /** 服务名称 */
        private final String serviceName;
        /** 环境标识（dev / staging / prod） */
        private final String environment;
        /** 配置版本号，用于变更检测 */
        private final int version;
        /** 数据库地址 */
        private final String dbHost;
        /** 超时时间（毫秒） */
        private final int timeoutMs;
        /** 订单审计功能开关 */
        private final boolean featureOrderAudit;

        public ServiceConfig(String serviceName, String environment, int version, String dbHost, int timeoutMs, boolean featureOrderAudit) {
            this.serviceName = serviceName;
            this.environment = environment;
            this.version = version;
            this.dbHost = dbHost;
            this.timeoutMs = timeoutMs;
            this.featureOrderAudit = featureOrderAudit;
        }

        public String getServiceName() { return serviceName; }
        public String getEnvironment() { return environment; }
        public int getVersion() { return version; }
        public String getDbHost() { return dbHost; }
        public int getTimeoutMs() { return timeoutMs; }
        public boolean isFeatureOrderAudit() { return featureOrderAudit; }
    }

    /**
     * ConfigCenter - 配置中心服务端
     *
     * 【设计模式】注册表模式：按 "serviceName@environment" 键存储配置。
     * 实际工程中这是独立部署的中间件（Apollo / Nacos）。
     */
    public static class ConfigCenter {
        /** 配置存储：key -> ServiceConfig */
        private final Map<String, ServiceConfig> store = new HashMap<>();

        /**
         * 发布配置。同一 key 重复发布会覆盖（支持配置更新）。
         */
        public void put(ServiceConfig config) {
            store.put(key(config.getServiceName(), config.getEnvironment()), config);
        }

        /**
         * 获取指定服务和环境的配置。
         */
        public ServiceConfig get(String serviceName, String environment) {
            return store.get(key(serviceName, environment));
        }
    }

    /**
     * ConfigClient - 配置客户端
     *
     * 【设计模式】代理模式：代理 ConfigCenter 的访问，本地缓存当前配置快照。
     * 客户端绑定特定的 serviceName + environment，通过 load/refresh 获取配置。
     *
     * 对比 Apollo 客户端：支持本地缓存文件、长轮询监听、配置变更回调。
     */
    public static class ConfigClient {
        private final ConfigCenter center;
        private final String serviceName;
        private final String environment;
        /** 当前缓存的配置快照 */
        private ServiceConfig current;

        public ConfigClient(ConfigCenter center, String serviceName, String environment) {
            this.center = center;
            this.serviceName = serviceName;
            this.environment = environment;
        }

        /** 首次加载配置 */
        public ServiceConfig load() {
            current = center.get(serviceName, environment);
            return current;
        }

        /** 刷新配置（从配置中心重新拉取） */
        public ServiceConfig refresh() {
            return load();
        }

        /** 获取当前缓存的配置快照 */
        public ServiceConfig current() {
            return current;
        }
    }

    /** 生成配置存储键 */
    private static String key(String serviceName, String environment) {
        return serviceName + "@" + environment;
    }
}

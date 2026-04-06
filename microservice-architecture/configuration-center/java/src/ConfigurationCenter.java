package src;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationCenter {

    public static class ServiceConfig {
        private final String serviceName;
        private final String environment;
        private final int version;
        private final String dbHost;
        private final int timeoutMs;
        private final boolean featureOrderAudit;

        public ServiceConfig(String serviceName, String environment, int version, String dbHost, int timeoutMs, boolean featureOrderAudit) {
            this.serviceName = serviceName;
            this.environment = environment;
            this.version = version;
            this.dbHost = dbHost;
            this.timeoutMs = timeoutMs;
            this.featureOrderAudit = featureOrderAudit;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getEnvironment() {
            return environment;
        }

        public int getVersion() {
            return version;
        }

        public String getDbHost() {
            return dbHost;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public boolean isFeatureOrderAudit() {
            return featureOrderAudit;
        }
    }

    public static class ConfigCenter {
        private final Map<String, ServiceConfig> store = new HashMap<>();

        public void put(ServiceConfig config) {
            store.put(key(config.getServiceName(), config.getEnvironment()), config);
        }

        public ServiceConfig get(String serviceName, String environment) {
            return store.get(key(serviceName, environment));
        }
    }

    public static class ConfigClient {
        private final ConfigCenter center;
        private final String serviceName;
        private final String environment;
        private ServiceConfig current;

        public ConfigClient(ConfigCenter center, String serviceName, String environment) {
            this.center = center;
            this.serviceName = serviceName;
            this.environment = environment;
        }

        public ServiceConfig load() {
            current = center.get(serviceName, environment);
            return current;
        }

        public ServiceConfig refresh() {
            return load();
        }

        public ServiceConfig current() {
            return current;
        }
    }

    private static String key(String serviceName, String environment) {
        return serviceName + "@" + environment;
    }
}

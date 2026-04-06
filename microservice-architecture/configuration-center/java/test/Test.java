package test;

import src.ConfigurationCenter;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        ConfigurationCenter.ConfigCenter center = new ConfigurationCenter.ConfigCenter();
        center.put(new ConfigurationCenter.ServiceConfig(
                "order-service", "prod", 1, "db.prod.internal", 300, false
        ));

        ConfigurationCenter.ConfigClient client = new ConfigurationCenter.ConfigClient(center, "order-service", "prod");
        ConfigurationCenter.ServiceConfig loaded = client.load();
        assertEquals(1, loaded.getVersion(), "initial version");
        assertEquals(300, loaded.getTimeoutMs(), "initial timeout");

        center.put(new ConfigurationCenter.ServiceConfig(
                "order-service", "prod", 2, "db.prod.internal", 500, true
        ));
        ConfigurationCenter.ServiceConfig refreshed = client.refresh();
        assertEquals(2, refreshed.getVersion(), "refreshed version");
        assertEquals(true, refreshed.isFeatureOrderAudit(), "feature flag");

        System.out.println("configuration-center(java) tests passed");
    }
}

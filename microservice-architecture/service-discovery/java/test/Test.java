package test;

import src.ServiceRegistry;

import java.util.List;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("inventory-service", new ServiceRegistry.ServiceInstance("inventory-a", "10.0.0.1:8081"));
        registry.register("inventory-service", new ServiceRegistry.ServiceInstance("inventory-b", "10.0.0.2:8081"));

        List<ServiceRegistry.ServiceInstance> instances = registry.instances("inventory-service");
        assertEquals(2, instances.size(), "registry should keep two instances");

        ServiceRegistry.RoundRobinDiscoverer discoverer = new ServiceRegistry.RoundRobinDiscoverer(registry);
        assertEquals("inventory-a", discoverer.next("inventory-service").getInstanceId(), "first instance");
        assertEquals("inventory-b", discoverer.next("inventory-service").getInstanceId(), "second instance");
        assertEquals("inventory-a", discoverer.next("inventory-service").getInstanceId(), "should cycle");

        assertEquals(true, registry.deregister("inventory-service", "inventory-a"), "deregister should succeed");
        assertEquals(1, registry.instances("inventory-service").size(), "one instance should remain");

        System.out.println("service-discovery(java) tests passed");
    }
}

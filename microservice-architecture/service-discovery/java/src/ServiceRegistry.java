package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRegistry {

    public static class ServiceInstance {
        private final String instanceId;
        private final String address;

        public ServiceInstance(String instanceId, String address) {
            this.instanceId = instanceId;
            this.address = address;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class RoundRobinDiscoverer {
        private final ServiceRegistry registry;
        private final Map<String, Integer> offsets = new HashMap<>();

        public RoundRobinDiscoverer(ServiceRegistry registry) {
            this.registry = registry;
        }

        public ServiceInstance next(String serviceName) {
            List<ServiceInstance> instances = registry.instances(serviceName);
            if (instances.isEmpty()) {
                return null;
            }
            int index = offsets.getOrDefault(serviceName, 0) % instances.size();
            offsets.put(serviceName, offsets.getOrDefault(serviceName, 0) + 1);
            return instances.get(index);
        }
    }

    private final Map<String, Map<String, ServiceInstance>> services = new HashMap<>();

    public void register(String serviceName, ServiceInstance instance) {
        services.computeIfAbsent(serviceName, ignored -> new HashMap<>())
                .put(instance.getInstanceId(), instance);
    }

    public boolean deregister(String serviceName, String instanceId) {
        Map<String, ServiceInstance> instances = services.get(serviceName);
        if (instances == null || !instances.containsKey(instanceId)) {
            return false;
        }
        instances.remove(instanceId);
        return true;
    }

    public List<ServiceInstance> instances(String serviceName) {
        List<ServiceInstance> instances = new ArrayList<>(services.getOrDefault(serviceName, Map.of()).values());
        instances.sort(Comparator.comparing(ServiceInstance::getInstanceId));
        return instances;
    }
}

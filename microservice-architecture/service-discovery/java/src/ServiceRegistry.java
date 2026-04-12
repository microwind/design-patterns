package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServiceRegistry - 服务发现模式的 Java 实现
 *
 * 【设计模式】
 *   - 注册表模式（Registry Pattern）：维护服务名到实例列表的全局映射。
 *   - 观察者模式（Observer Pattern）：实际工程中注册中心会通知订阅者实例变更，
 *     本示例简化为主动查询（pull 模式）。
 *   - 策略模式（Strategy Pattern）：RoundRobinDiscoverer 封装轮询选择策略。
 *
 * 【架构思想】
 *   服务发现解决"调用方如何找到被调服务"的问题。当服务实例动态扩缩容时，
 *   调用方通过注册中心获取可用实例地址，而非硬编码。
 *
 * 【开源对比】
 *   - Eureka：Netflix 的 AP 模式注册中心，通过心跳维持注册
 *   - Nacos：阿里巴巴的注册中心，支持 AP/CP 切换和 push 通知
 *   - Consul：HashiCorp 的 CP 模式注册中心，基于 Raft 共识
 *   本示例用内存 Map 简化，省略了心跳、健康检查和集群同步。
 */
public class ServiceRegistry {

    /**
     * ServiceInstance - 服务实例（值对象）
     *
     * 每个实例由唯一的 instanceId 和网络地址 address 标识。
     */
    public static class ServiceInstance {
        /** 实例唯一标识（如 "order-1"） */
        private final String instanceId;
        /** 实例网络地址（如 "http://10.0.0.1:8080"） */
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

    /**
     * RoundRobinDiscoverer - 轮询服务发现客户端
     *
     * 【设计模式】策略模式：封装了轮询选择策略。
     * 实际工程中 Ribbon/LoadBalancer 支持多种策略（随机、加权、最少连接）。
     */
    public static class RoundRobinDiscoverer {
        private final ServiceRegistry registry;
        /** 每个服务的轮询偏移量 */
        private final Map<String, Integer> offsets = new HashMap<>();

        public RoundRobinDiscoverer(ServiceRegistry registry) {
            this.registry = registry;
        }

        /**
         * 获取下一个可用实例（轮询策略）。
         *
         * @param serviceName 服务名称
         * @return 下一个实例，无可用实例时返回 null
         */
        public ServiceInstance next(String serviceName) {
            List<ServiceInstance> instances = registry.instances(serviceName);
            if (instances.isEmpty()) {
                return null;
            }
            // 取模实现轮询
            int index = offsets.getOrDefault(serviceName, 0) % instances.size();
            offsets.put(serviceName, offsets.getOrDefault(serviceName, 0) + 1);
            return instances.get(index);
        }
    }

    /** 服务注册表：服务名 -> (实例ID -> 实例) */
    private final Map<String, Map<String, ServiceInstance>> services = new HashMap<>();

    /**
     * 注册服务实例。
     * 同一 instanceId 重复注册会覆盖旧实例（幂等操作）。
     *
     * @param serviceName 服务名称
     * @param instance    服务实例
     */
    public void register(String serviceName, ServiceInstance instance) {
        // computeIfAbsent 确保服务名对应的 Map 存在
        services.computeIfAbsent(serviceName, ignored -> new HashMap<>())
                .put(instance.getInstanceId(), instance);
    }

    /**
     * 摘除服务实例。
     * 实例下线或健康检查失败时调用。
     *
     * @param serviceName 服务名称
     * @param instanceId  实例ID
     * @return true=摘除成功，false=实例不存在
     */
    public boolean deregister(String serviceName, String instanceId) {
        Map<String, ServiceInstance> instances = services.get(serviceName);
        if (instances == null || !instances.containsKey(instanceId)) {
            return false;
        }
        instances.remove(instanceId);
        return true;
    }

    /**
     * 获取指定服务的所有可用实例（按 instanceId 排序，保证轮询稳定）。
     *
     * @param serviceName 服务名称
     * @return 实例列表（按 ID 排序）
     */
    public List<ServiceInstance> instances(String serviceName) {
        List<ServiceInstance> instances = new ArrayList<>(services.getOrDefault(serviceName, Map.of()).values());
        // 排序保证轮询结果的确定性
        instances.sort(Comparator.comparing(ServiceInstance::getInstanceId));
        return instances;
    }
}

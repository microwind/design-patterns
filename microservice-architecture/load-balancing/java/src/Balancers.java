package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Balancers - 负载均衡模式的 Java 实现
 *
 * 本模块演示微服务架构中三种经典负载均衡算法：轮询（Round-Robin）、
 * 加权轮询（Weighted Round-Robin）和最少连接（Least Connections）。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：三种负载均衡算法（RoundRobin / WeightedRoundRobin /
 *     LeastConnections）是可互换的策略。调用方根据场景选择不同策略，接口统一。
 *     实际工程中通常抽象出 LoadBalancer 接口，各算法作为具体策略类实现。
 *   - 迭代器模式（Iterator Pattern）：RoundRobin 和 WeightedRoundRobin 的 next()
 *     方法按序遍历后端列表，通过取模实现循环迭代。
 *
 * 【架构思想】
 *   负载均衡将流量分散到多个后端实例，避免单点过载。轮询适用于同质节点，
 *   加权轮询适用于异构节点（按性能分配权重），最少连接适用于请求耗时差异大的场景。
 *
 * 【开源对比】
 *   - Nginx：支持 round-robin、weighted、least_conn、ip_hash 等多种策略
 *   - Spring Cloud LoadBalancer：基于 ServiceInstance 列表的客户端负载均衡
 *   - Envoy：支持 round-robin、least-request、random、ring-hash 等策略
 *   本示例省略了健康检查、权重动态调整、一致性哈希等工程细节，聚焦于算法核心。
 */
public class Balancers {

    public static class Backend {
        private final String backendId;
        private final int weight;
        private int activeConnections;

        public Backend(String backendId, int weight, int activeConnections) {
            this.backendId = backendId;
            this.weight = weight;
            this.activeConnections = activeConnections;
        }

        public String getBackendId() {
            return backendId;
        }

        public int getWeight() {
            return weight;
        }

        public int getActiveConnections() {
            return activeConnections;
        }

        public void incrementConnections() {
            activeConnections++;
        }

        public void decrementConnections() {
            if (activeConnections > 0) {
                activeConnections--;
            }
        }
    }

    public static class RoundRobinBalancer {
        private final List<Backend> backends;
        private int nextIndex;

        public RoundRobinBalancer(List<Backend> backends) {
            this.backends = backends;
        }

        public Backend next() {
            Backend backend = backends.get(nextIndex % backends.size());
            nextIndex++;
            return backend;
        }
    }

    public static class WeightedRoundRobinBalancer {
        private final List<Backend> sequence = new ArrayList<>();
        private int nextIndex;

        public WeightedRoundRobinBalancer(List<Backend> backends) {
            for (Backend backend : backends) {
                int repeat = Math.max(1, backend.getWeight());
                for (int i = 0; i < repeat; i++) {
                    sequence.add(backend);
                }
            }
        }

        public Backend next() {
            Backend backend = sequence.get(nextIndex % sequence.size());
            nextIndex++;
            return backend;
        }
    }

    public static class LeastConnectionsBalancer {
        private final Map<String, Backend> backends = new HashMap<>();

        public LeastConnectionsBalancer(List<Backend> backends) {
            for (Backend backend : backends) {
                this.backends.put(
                        backend.getBackendId(),
                        new Backend(backend.getBackendId(), backend.getWeight(), backend.getActiveConnections())
                );
            }
        }

        public Backend acquire() {
            Backend chosen = null;
            for (Backend backend : backends.values()) {
                if (chosen == null || backend.getActiveConnections() < chosen.getActiveConnections()) {
                    chosen = backend;
                }
            }
            chosen.incrementConnections();
            return chosen;
        }

        public void release(String backendId) {
            Backend backend = backends.get(backendId);
            if (backend != null) {
                backend.decrementConnections();
            }
        }
    }
}

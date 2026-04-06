package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

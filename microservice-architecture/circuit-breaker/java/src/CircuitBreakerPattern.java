package src;

public class CircuitBreakerPattern {
    public static class CircuitBreaker {
        private final int failureThreshold;
        private int failures;
        private String state = "closed";

        public CircuitBreaker(int failureThreshold) {
            this.failureThreshold = failureThreshold;
        }

        public void recordFailure() {
            if ("closed".equals(state)) {
                failures++;
                if (failures >= failureThreshold) {
                    state = "open";
                }
            }
        }

        public void probe(boolean success) {
            if (!"open".equals(state)) {
                return;
            }
            state = "half-open";
            if (success) {
                state = "closed";
                failures = 0;
            } else {
                state = "open";
            }
        }

        public String getState() {
            return state;
        }
    }
}

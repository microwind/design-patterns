package src;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResiliencePatterns {

    public static class OperationTimeoutException extends RuntimeException {
        public OperationTimeoutException(String message) {
            super(message);
        }
    }

    public static class CircuitOpenException extends RuntimeException {
        public CircuitOpenException(String message) {
            super(message);
        }
    }

    public static class Result {
        private final String value;
        private final Exception error;
        private final long delayMillis;

        public Result(String value, Exception error, long delayMillis) {
            this.value = value;
            this.error = error;
            this.delayMillis = delayMillis;
        }
    }

    public static class ScriptedDependency {
        private final List<Result> results;
        private int index;

        public ScriptedDependency(List<Result> results) {
            this.results = results;
        }

        public String call() throws Exception {
            if (results.isEmpty()) {
                throw new RuntimeException("no scripted result available");
            }

            int currentIndex = Math.min(index, results.size() - 1);
            Result result = results.get(currentIndex);
            index++;

            if (result.delayMillis > 0) {
                Thread.sleep(result.delayMillis);
            }
            if (result.error != null) {
                throw result.error;
            }
            return result.value;
        }
    }

    public static String callWithTimeout(Duration timeout, Callable<String> operation) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(operation);
        try {
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new OperationTimeoutException("operation timed out");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw new RuntimeException(cause);
        } finally {
            executor.shutdownNow();
        }
    }

    public static RetryOutcome retry(int maxAttempts, Callable<String> operation) throws Exception {
        int attempts = Math.max(1, maxAttempts);
        Exception lastError = null;
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                return new RetryOutcome(operation.call(), attempt);
            } catch (Exception ex) {
                lastError = ex;
            }
        }
        throw lastError;
    }

    public record RetryOutcome(String value, int attempts) {}

    public static class CircuitBreaker {
        private final int failureThreshold;
        private int consecutiveFailures;
        private boolean open;

        public CircuitBreaker(int failureThreshold) {
            this.failureThreshold = Math.max(1, failureThreshold);
        }

        public String execute(Callable<String> operation, String fallback) throws Exception {
            if (open) {
                throw new CircuitOpenException(fallback);
            }

            try {
                String value = operation.call();
                consecutiveFailures = 0;
                return value;
            } catch (Exception ex) {
                consecutiveFailures++;
                if (consecutiveFailures >= failureThreshold) {
                    open = true;
                }
                return fallback;
            }
        }

        public void reset() {
            consecutiveFailures = 0;
            open = false;
        }
    }
}

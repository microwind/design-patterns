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

/**
 * ResiliencePatterns - 弹性模式组合的 Java 实现
 *
 * 本模块将超时、重试、断路器三种弹性模式组合在一起，展示它们如何协同工作
 * 来保护微服务调用方免受下游故障影响。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：超时、重试、断路器是三种可独立使用或组合的
 *     弹性策略，调用方根据场景选择合适的策略组合。
 *   - 代理模式（Proxy Pattern）：callWithTimeout / retry / CircuitBreaker.execute
 *     包裹在真实操作之外，透明地添加弹性行为。
 *   - 状态模式（State Pattern）：CircuitBreaker 在 closed/open 状态下行为不同，
 *     open 时直接抛出异常，closed 时执行操作并跟踪失败。
 *   - 模板方法模式（Template Method）：retry 定义了"循环调用 → 判断 → 继续/停止"
 *     的固定骨架，具体操作由调用方以 Callable 提供。
 *
 * 【架构思想】
 *   弹性模式组合是微服务架构的核心防护层：超时防止无限等待，重试处理暂时性故障，
 *   断路器阻止级联雪崩。三者通常组合使用：超时包裹在最内层，重试在中间，断路器在最外层。
 *
 * 【开源对比】
 *   - Resilience4j：Java 生态最流行的弹性库，提供 CircuitBreaker / Retry /
 *     TimeLimiter / Bulkhead / RateLimiter 等模块，支持组合和装饰器链
 *   - Netflix Hystrix（已停止维护）：最早的 Java 断路器库
 *   - Failsafe：轻量级 Java 弹性库，支持策略组合
 *   本示例省略了指数退避、滑动窗口、线程池隔离等工程细节，聚焦于三种模式的核心逻辑。
 */
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

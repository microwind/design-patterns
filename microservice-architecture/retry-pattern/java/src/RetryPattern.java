package src;

/**
 * RetryPattern - 重试模式的 Java 实现
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：operation 作为策略（BooleanSupplier）传入 retry。
 *   - 模板方法模式（Template Method）：retry 定义了循环调用的骨架。
 *
 * 【架构思想】
 *   重试处理暂时性故障，但必须控制最大次数，避免加重下游负担。
 *
 * 【开源对比】
 *   - Resilience4j Retry：支持指数退避、可重试异常过滤、事件监听
 *   - Spring Retry：@Retryable 注解 + RetryTemplate
 *   - Failsafe（Java）：轻量级重试库
 *   本示例实现最简单的固定次数重试，省略了退避和异常分类。
 */
public class RetryPattern {

    /**
     * ScriptedOperation - 脚本化操作（测试辅助）
     * 模拟"前 N 次失败，之后成功"的场景。
     */
    public static class ScriptedOperation {
        /** 成功前需要失败的次数 */
        private final int failuresBeforeSuccess;
        /** 当前已尝试次数 */
        private int attempts;

        public ScriptedOperation(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        /** 调用操作。前 failuresBeforeSuccess 次返回 false，之后返回 true。 */
        public boolean call() {
            attempts++;
            return attempts > failuresBeforeSuccess;
        }
    }

    /**
     * RetryResult - 重试结果
     * @param ok       是否最终成功
     * @param attempts 实际尝试次数
     */
    public record RetryResult(boolean ok, int attempts) {}

    /**
     * 执行重试。循环调用操作，成功时立即返回，超过最大次数则返回失败。
     *
     * @param maxAttempts 最大尝试次数
     * @param operation   待重试的操作（返回 true=成功，false=失败）
     * @return 重试结果
     */
    public static RetryResult retry(int maxAttempts, java.util.function.BooleanSupplier operation) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            // 调用操作，成功则立即返回
            if (operation.getAsBoolean()) {
                return new RetryResult(true, attempt);
            }
        }
        // 达到最大次数仍失败
        return new RetryResult(false, maxAttempts);
    }
}

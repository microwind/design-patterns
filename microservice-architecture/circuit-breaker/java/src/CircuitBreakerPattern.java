package src;

/**
 * CircuitBreakerPattern - 断路器模式的 Java 实现
 *
 * 【设计模式】
 *   - 状态模式（State Pattern）：断路器包含 closed / open / half-open 三种状态，
 *     不同状态下 recordFailure() 和 probe() 的行为不同。本示例用字符串 + 条件分支
 *     简化了状态模式的实现；在实际工程中（如 Resilience4j）通常会将每种状态抽象为
 *     独立的状态类（ClosedState, OpenState, HalfOpenState）。
 *   - 代理模式（Proxy Pattern）：断路器包裹在真实服务调用之外，调用方通过断路器
 *     间接访问下游服务。当断路器处于 open 状态时，请求不会到达下游。
 *
 * 【架构思想】
 *   断路器是微服务弹性设计的核心组件。它防止调用方在下游故障时不断重试导致级联雪崩，
 *   同时通过探测机制实现自动恢复，无需人工干预。
 *
 * 【开源对比】
 *   - Resilience4j CircuitBreaker：基于滑动窗口的失败率统计，支持线程安全和 Micrometer 监控
 *   - Netflix Hystrix（已停止维护）：基于滑动窗口 + 线程隔离的断路器实现
 *   - .NET Polly：支持基于异常类型的断路器策略
 *   本示例简化了失败判定逻辑（简单计数 vs 滑动窗口），省略了定时器和线程安全，
 *   聚焦于状态机转换的核心骨架。
 */
public class CircuitBreakerPattern {

    /**
     * CircuitBreaker - 断路器状态机
     *
     * 核心状态转换：
     *   closed → open（连续失败达到阈值）
     *   open → half-open → closed（探测成功）
     *   open → half-open → open（探测失败）
     */
    public static class CircuitBreaker {
        /** 触发熔断的连续失败阈值 */
        private final int failureThreshold;

        /** 当前连续失败次数 */
        private int failures;

        /** 当前状态：closed / open / half-open */
        private String state = "closed";

        /**
         * 构造断路器
         *
         * @param failureThreshold 连续失败多少次后触发熔断（切换到 open）
         */
        public CircuitBreaker(int failureThreshold) {
            this.failureThreshold = failureThreshold;
        }

        /**
         * 记录一次失败调用。
         * 仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open 状态。
         * 在 open 状态下调用此方法无效（已经熔断，不再累加）。
         */
        public void recordFailure() {
            // 只有在闭合状态下才统计失败
            if ("closed".equals(state)) {
                failures++;
                // 失败次数达到阈值，打开断路器
                if (failures >= failureThreshold) {
                    state = "open";
                }
            }
        }

        /**
         * 在 open 状态下进行一次探测调用。
         * 先进入 half-open 状态，再根据探测结果决定最终状态。
         *
         * @param success 探测调用是否成功
         *                true  → 恢复 closed，清零失败计数
         *                false → 回到 open，继续熔断
         */
        public void probe(boolean success) {
            // 非 open 状态下不需要探测
            if (!"open".equals(state)) {
                return;
            }
            // 进入半开状态，允许一次试探调用
            state = "half-open";
            if (success) {
                // 探测成功，恢复正常
                state = "closed";
                failures = 0;
            } else {
                // 探测失败，重新熔断
                state = "open";
            }
        }

        /**
         * 获取断路器当前状态
         *
         * @return "closed" / "open" / "half-open"
         */
        public String getState() {
            return state;
        }
    }
}

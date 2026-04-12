package src;

/**
 * FixedWindowLimiter - 固定窗口限流器
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：固定窗口是一种限流策略。实际工程中
 *     还有令牌桶、滑动窗口、漏桶等策略，可通过策略模式切换。
 *
 * 【架构思想】
 *   限流保护系统不被过载流量拖垮。当请求数达到窗口上限时拒绝新请求，
 *   窗口推进后重新接受。
 *
 * 【开源对比】
 *   - Sentinel（阿里）：滑动窗口 + 多维度限流 + 降级
 *   - Resilience4j RateLimiter：令牌桶/滑动窗口 + Micrometer 监控
 *   - Guava RateLimiter：令牌桶实现
 *   本示例实现最简单的固定窗口，省略了时间窗口和线程安全。
 */
public class FixedWindowLimiter {
    /** 窗口内最大允许请求数 */
    private final int limit;
    /** 当前窗口内已通过的请求数 */
    private int count;

    /**
     * @param limit 窗口内最大允许请求数
     */
    public FixedWindowLimiter(int limit) {
        this.limit = limit;
    }

    /**
     * 判断是否允许通过。
     * count < limit 时放行并递增计数，否则拒绝。
     *
     * @return true=放行，false=拒绝（限流）
     */
    public boolean allow() {
        // 达到上限，拒绝请求
        if (count >= limit) {
            return false;
        }
        // 放行并递增计数
        count++;
        return true;
    }

    /**
     * 推进窗口，重置计数。
     * 实际工程中由定时器自动触发，本示例由外部手动调用。
     */
    public void advanceWindow() {
        count = 0;
    }
}

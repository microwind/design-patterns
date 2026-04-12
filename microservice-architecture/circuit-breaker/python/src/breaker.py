"""
breaker.py - 断路器模式（Circuit Breaker Pattern）的 Python 实现

【设计模式】
  - 状态模式（State Pattern）：断路器在 closed / open / half-open 三种状态下行为不同。
    本示例用字符串属性 + 条件分支简化实现；在实际工程中（如 pybreaker）通常会用
    独立的状态类来封装不同状态的行为。
  - 代理模式（Proxy Pattern）：断路器包裹在真实服务调用之外，对调用方透明地拦截请求。

【架构思想】
  断路器防止调用方在下游故障时持续重试导致级联雪崩，通过探测机制实现自动恢复。

【开源对比】
  - pybreaker：Python 最流行的断路器库，支持最大失败次数、超时重置、状态变更监听器、
    排除特定异常等功能。
  - tenacity + circuit breaker：tenacity 库本身侧重重试，可与断路器组合使用。
  本示例省略了时间窗口、线程安全、异常类型区分等工程细节，聚焦于状态机核心。
"""


class CircuitBreaker:
    """断路器状态机。

    状态转换：
        closed → open（连续失败达到阈值）
        open → half-open → closed（探测成功）
        open → half-open → open（探测失败）

    属性:
        failure_threshold: 触发熔断的连续失败阈值
        failures: 当前连续失败次数
        state: 当前状态（"closed" / "open" / "half-open"）
    """

    def __init__(self, failure_threshold: int) -> None:
        """初始化断路器。

        Args:
            failure_threshold: 连续失败多少次后触发熔断
        """
        self.failure_threshold = failure_threshold
        self.failures = 0
        self.state = "closed"

    def record_failure(self) -> None:
        """记录一次失败调用。

        仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open。
        对比 pybreaker：生产环境中还会考虑异常类型过滤（某些异常不计入失败）。
        """
        # 只有闭合状态下才统计失败
        if self.state == "closed":
            self.failures += 1
            # 失败次数达到阈值，打开断路器
            if self.failures >= self.failure_threshold:
                self.state = "open"

    def probe(self, success: bool) -> None:
        """在 open 状态下进行一次探测调用。

        先转为 half-open，再根据探测结果决定最终状态。

        Args:
            success: 探测调用是否成功
                     True  → 恢复 closed，清零失败计数
                     False → 回到 open，继续熔断

        对比 pybreaker：open → half-open 由内置定时器自动触发（超时后自动进入 half-open），
        而非外部手动调用。
        """
        # 非 open 状态下不需要探测
        if self.state != "open":
            return
        # 进入半开状态，允许一次试探调用
        self.state = "half-open"
        if success:
            # 探测成功，恢复正常
            self.state = "closed"
            self.failures = 0
        else:
            # 探测失败，重新熔断
            self.state = "open"

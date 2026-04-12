"""
limiter.py - 限流模式（Rate Limiting）的 Python 实现

【设计模式】策略模式：固定窗口是一种限流策略。

【架构思想】限流保护系统不被过载流量拖垮。

【开源对比】
  - Flask-Limiter：Flask 限流扩展，支持多种策略
  - limits 库：Python 限流库，支持固定窗口/滑动窗口/令牌桶
  本示例实现固定窗口，省略了时间窗口。
"""


class FixedWindowLimiter:
    """固定窗口限流器。

    【设计模式】策略模式：固定窗口是一种限流策略。

    Attributes:
        limit: 窗口内最大允许请求数
        count: 当前窗口已通过请求数
    """

    def __init__(self, limit: int) -> None:
        self.limit = limit
        self.count = 0

    def allow(self) -> bool:
        """判断是否允许通过。count < limit 时放行，否则拒绝。"""
        # 达到上限，拒绝
        if self.count >= self.limit:
            return False
        # 放行并递增
        self.count += 1
        return True

    def advance_window(self) -> None:
        """推进窗口，重置计数。"""
        self.count = 0

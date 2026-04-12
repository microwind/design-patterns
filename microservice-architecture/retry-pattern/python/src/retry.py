"""
retry.py - 重试模式（Retry Pattern）的 Python 实现

【设计模式】
  - 策略模式：operation 作为可调用对象传入 retry。
  - 模板方法模式：retry 定义了循环调用的骨架。

【架构思想】重试处理暂时性故障，但必须控制最大次数。

【开源对比】
  - tenacity：Python 最流行的重试库，支持指数退避、条件重试、超时
  - retrying：轻量级重试装饰器
  本示例实现固定次数重试。
"""


class ScriptedOperation:
    """脚本化操作（测试辅助）。模拟"前 N 次失败，之后成功"。

    Attributes:
        failures_before_success: 成功前需要失败的次数
        attempts: 当前已尝试次数
    """

    def __init__(self, failures_before_success: int) -> None:
        self.failures_before_success = failures_before_success
        self.attempts = 0

    def call(self) -> bool:
        """调用操作。前 failures_before_success 次返回 False，之后返回 True。"""
        self.attempts += 1
        return self.attempts > self.failures_before_success


def retry(max_attempts: int, operation) -> tuple[bool, int]:
    """执行重试。循环调用操作，成功时立即返回。

    Args:
        max_attempts: 最大尝试次数
        operation: 可调用对象，返回 True=成功

    Returns:
        (是否成功, 实际尝试次数)
    """
    for attempt in range(1, max_attempts + 1):
        # 调用操作，成功则立即返回
        if operation():
            return True, attempt
    # 达到最大次数仍失败
    return False, max_attempts

"""
feature_flag.py - 特性开关模式（Feature Flag Pattern）的 Python 实现

【设计模式】
  - 策略模式：不同开关配置代表不同发布策略。
  - 观察者模式：实际工程中开关变更会推送通知。

【架构思想】特性开关实现"功能发布与代码发布解耦"。

【开源对比】
  - unleash-client-python：Unleash 的 Python SDK
  - flagsmith：Flagsmith 的 Python SDK
  本示例用内存字典 + 白名单简化。
"""

from dataclasses import dataclass
from typing import Dict


@dataclass
class FeatureFlag:
    """开关配置。

    Attributes:
        default_enabled: 默认是否启用
        allowlist: 白名单（userId -> True 表示启用）
    """
    default_enabled: bool
    allowlist: Dict[str, bool]


class FeatureFlagService:
    """特性开关服务。管理多个开关的注册和评估。"""

    def __init__(self) -> None:
        self.flags: Dict[str, FeatureFlag] = {}

    def set(self, flag: str, config: FeatureFlag) -> None:
        """注册或更新开关配置。"""
        self.flags[flag] = config

    def enabled(self, flag: str, user_id: str) -> bool:
        """评估开关是否对指定用户启用。白名单优先 → 默认值兜底。"""
        config = self.flags.get(flag)
        if config is None:
            return False  # 未注册默认禁用
        if config.allowlist.get(user_id, False):
            return True  # 白名单优先
        return config.default_enabled  # 兜底

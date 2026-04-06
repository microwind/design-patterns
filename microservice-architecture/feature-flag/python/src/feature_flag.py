from dataclasses import dataclass
from typing import Dict


@dataclass
class FeatureFlag:
    default_enabled: bool
    allowlist: Dict[str, bool]


class FeatureFlagService:
    def __init__(self) -> None:
        self.flags: Dict[str, FeatureFlag] = {}

    def set(self, flag: str, config: FeatureFlag) -> None:
        self.flags[flag] = config

    def enabled(self, flag: str, user_id: str) -> bool:
        config = self.flags.get(flag)
        if config is None:
            return False
        if config.allowlist.get(user_id, False):
            return True
        return config.default_enabled

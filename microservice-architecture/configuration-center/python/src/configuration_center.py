"""
configuration_center.py - 配置中心模式（Configuration Center Pattern）的 Python 实现

【设计模式】
  - 观察者模式：实际工程中配置变更会推送通知，本示例简化为客户端主动 refresh。
  - 单例模式：ConfigCenter 通常全局唯一。
  - 代理模式：ConfigClient 代理 ConfigCenter 访问并缓存配置。

【架构思想】
  配置中心将所有服务配置集中存储，按"服务名+环境"维度管理，
  支持客户端运行时刷新，无需重新部署。

【开源对比】
  - nacos-sdk-python：Nacos 配置管理客户端
  - python-etcd3：etcd 的 Python 客户端，支持 watch
  本示例用内存字典简化，省略了持久化和推送。
"""

from dataclasses import dataclass
from typing import Dict, Optional, Tuple


@dataclass
class ServiceConfig:
    """服务配置（数据类）。

    Attributes:
        service_name: 服务名称
        environment: 环境标识（dev / staging / prod）
        version: 配置版本号，用于变更检测
        db_host: 数据库地址
        timeout_ms: 超时时间（毫秒）
        feature_order_audit: 订单审计功能开关
    """
    service_name: str
    environment: str
    version: int
    db_host: str
    timeout_ms: int
    feature_order_audit: bool


class ConfigCenter:
    """配置中心服务端。

    【设计模式】注册表模式：按 (service_name, environment) 元组键存储配置。
    """

    def __init__(self) -> None:
        # 配置存储：(服务名, 环境) -> 配置
        self._store: Dict[Tuple[str, str], ServiceConfig] = {}

    def put(self, config: ServiceConfig) -> None:
        """发布配置。同一 key 重复发布会覆盖（支持配置更新）。"""
        self._store[(config.service_name, config.environment)] = config

    def get(self, service_name: str, environment: str) -> Optional[ServiceConfig]:
        """获取指定服务和环境的配置。"""
        return self._store.get((service_name, environment))


class ConfigClient:
    """配置客户端。

    【设计模式】代理模式：代理 ConfigCenter 访问，本地缓存当前配置快照。
    """

    def __init__(self, center: ConfigCenter, service_name: str, environment: str) -> None:
        self._center = center
        self._service_name = service_name
        self._environment = environment
        # 当前缓存的配置快照
        self._current: Optional[ServiceConfig] = None

    def load(self) -> Optional[ServiceConfig]:
        """首次加载配置（从配置中心拉取并缓存）。"""
        config = self._center.get(self._service_name, self._environment)
        self._current = config
        return config

    def refresh(self) -> Optional[ServiceConfig]:
        """刷新配置（从配置中心重新拉取）。"""
        return self.load()

    def current(self) -> Optional[ServiceConfig]:
        """获取当前缓存的配置快照。"""
        return self._current

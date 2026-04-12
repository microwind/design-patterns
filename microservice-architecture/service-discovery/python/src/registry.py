"""
registry.py - 服务发现模式（Service Discovery Pattern）的 Python 实现

【设计模式】
  - 注册表模式（Registry Pattern）：ServiceRegistry 维护服务名到实例列表的映射。
  - 策略模式（Strategy Pattern）：RoundRobinDiscoverer 封装轮询选择策略。

【架构思想】
  服务发现解决微服务架构中"调用方如何找到被调服务"的问题。

【开源对比】
  - python-consul：Python 的 Consul 客户端
  - nacos-sdk-python：Nacos 的 Python SDK
  本示例用内存字典简化，省略了心跳、健康检查和网络通信。
"""

from dataclasses import dataclass
from typing import Dict, List, Optional


@dataclass(frozen=True)
class ServiceInstance:
    """服务实例（不可变值对象）。

    Attributes:
        instance_id: 实例唯一标识（如 "order-1"）
        address: 实例网络地址（如 "http://10.0.0.1:8080"）
    """
    instance_id: str
    address: str


class ServiceRegistry:
    """服务注册中心。

    【设计模式】注册表模式：维护服务名到实例列表的全局映射。
    """

    def __init__(self) -> None:
        # 服务注册表：服务名 -> {实例ID -> 实例}
        self._services: Dict[str, Dict[str, ServiceInstance]] = {}

    def register(self, service_name: str, instance: ServiceInstance) -> None:
        """注册服务实例。同一 instance_id 重复注册会覆盖（幂等）。"""
        self._services.setdefault(service_name, {})
        self._services[service_name][instance.instance_id] = instance

    def deregister(self, service_name: str, instance_id: str) -> bool:
        """摘除服务实例。返回是否摘除成功。"""
        instances = self._services.get(service_name)
        if not instances or instance_id not in instances:
            return False
        del instances[instance_id]
        return True

    def instances(self, service_name: str) -> List[ServiceInstance]:
        """获取指定服务的所有可用实例（按 instance_id 排序，保证轮询稳定）。"""
        instances = list(self._services.get(service_name, {}).values())
        return sorted(instances, key=lambda item: item.instance_id)


class RoundRobinDiscoverer:
    """轮询服务发现客户端。

    【设计模式】策略模式：封装轮询选择策略。
    """

    def __init__(self, registry: ServiceRegistry) -> None:
        self._registry = registry
        self._offsets: Dict[str, int] = {}

    def next(self, service_name: str) -> Optional[ServiceInstance]:
        """获取下一个可用实例（轮询策略）。无可用实例时返回 None。"""
        instances = self._registry.instances(service_name)
        if not instances:
            return None
        # 取模实现轮询
        index = self._offsets.get(service_name, 0) % len(instances)
        self._offsets[service_name] = self._offsets.get(service_name, 0) + 1
        return instances[index]

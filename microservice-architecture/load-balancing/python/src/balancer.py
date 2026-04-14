"""
balancer.py - 负载均衡模式（Load Balancing Pattern）的 Python 实现

本模块演示三种经典负载均衡算法：轮询、加权轮询和最少连接。

【设计模式】
  - 策略模式（Strategy Pattern）：三种负载均衡算法是可互换的策略，
    各自实现不同的分配逻辑，调用方根据场景选择。
  - 迭代器模式（Iterator Pattern）：RoundRobin 和 WeightedRoundRobin
    的 next() 方法通过取模实现循环迭代。

【架构思想】
  负载均衡将流量分散到多个后端实例，避免单点过载。

【开源对比】
  - HAProxy（可通过 Python 脚本配置）：支持 roundrobin、leastconn、source 等策略
  - httpx / aiohttp：Python HTTP 客户端，可配合自定义负载均衡逻辑使用
  本示例省略了健康检查和动态权重调整等工程细节，聚焦于算法核心。
"""

from dataclasses import dataclass
from typing import Dict, List


@dataclass
class Backend:
    backend_id: str
    weight: int = 1
    active_connections: int = 0


class RoundRobinBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._backends = list(backends)
        self._next = 0

    def next(self) -> Backend:
        backend = self._backends[self._next % len(self._backends)]
        self._next += 1
        return backend


class WeightedRoundRobinBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._sequence: List[Backend] = []
        for backend in backends:
            repeat = backend.weight if backend.weight > 0 else 1
            self._sequence.extend([backend] * repeat)
        self._next = 0

    def next(self) -> Backend:
        backend = self._sequence[self._next % len(self._sequence)]
        self._next += 1
        return backend


class LeastConnectionsBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._backends: Dict[str, Backend] = {
            backend.backend_id: Backend(
                backend_id=backend.backend_id,
                weight=backend.weight,
                active_connections=backend.active_connections,
            )
            for backend in backends
        }

    def acquire(self) -> Backend:
        backend = min(self._backends.values(), key=lambda item: item.active_connections)
        backend.active_connections += 1
        return backend

    def release(self, backend_id: str) -> None:
        backend = self._backends.get(backend_id)
        if backend and backend.active_connections > 0:
            backend.active_connections -= 1

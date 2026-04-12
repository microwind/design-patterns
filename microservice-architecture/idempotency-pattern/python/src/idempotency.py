"""
idempotency.py - 幂等模式（Idempotency Pattern）的 Python 实现

【设计模式】
  - 备忘录模式（Memento Pattern）：首次执行结果被存储，后续重复请求返回备忘结果。
  - 代理模式（Proxy Pattern）：幂等层包裹在业务逻辑之外，透明拦截重复请求。

【架构思想】
  幂等模式通过 idempotency_key 将重复请求折叠为同一结果。

【开源对比】
  - Python + Redis：通过 redis.setnx + TTL 实现幂等锁
  - Django / FastAPI 中间件：在请求层统一处理幂等
  本示例用内存字典简化。
"""

from dataclasses import dataclass
from typing import Dict, Tuple


@dataclass
class OrderResponse:
    """订单响应。replayed 字段区分首次/重复请求。

    Attributes:
        order_id: 订单ID
        sku: 商品SKU
        quantity: 数量
        status: 状态（CREATED / CONFLICT）
        replayed: 是否为重放结果
    """
    order_id: str
    sku: str
    quantity: int
    status: str
    replayed: bool


class IdempotencyOrderService:
    """带幂等保护的订单服务。

    三条路径：
      1. 首次请求 → CREATED
      2. 重复请求 + 指纹匹配 → 返回存储结果（replayed=True）
      3. 重复请求 + 指纹不匹配 → CONFLICT
    """

    def __init__(self) -> None:
        # 幂等存储：key → (fingerprint, response)
        self._store: Dict[str, Tuple[str, OrderResponse]] = {}

    def create_order(self, idempotency_key: str, order_id: str, sku: str, quantity: int) -> OrderResponse:
        """创建订单（带幂等保护）。"""
        # 计算请求指纹
        fingerprint = f"{order_id}|{sku}|{quantity}"
        existing = self._store.get(idempotency_key)
        if existing is not None:
            stored_fingerprint, stored_response = existing
            # 同一幂等键但参数不同 → 冲突
            if stored_fingerprint != fingerprint:
                return OrderResponse(order_id, sku, quantity, "CONFLICT", False)
            # 同一幂等键且参数相同 → 返回存储的结果
            return OrderResponse(
                stored_response.order_id,
                stored_response.sku,
                stored_response.quantity,
                stored_response.status,
                True,
            )

        # 首次请求 → 执行业务逻辑并存储结果
        response = OrderResponse(order_id, sku, quantity, "CREATED", False)
        self._store[idempotency_key] = (fingerprint, response)
        return response

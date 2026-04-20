"""Order 聚合根（纯 Python）。

保留一套等价于 gin-ddd / nestjs-ddd 的状态机：
  PENDING → PAID → SHIPPED → DELIVERED
  PENDING → CANCELLED
  PAID|SHIPPED → REFUNDED
"""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime
from decimal import Decimal
from enum import Enum

from shared.infrastructure.exceptions import DomainError, ValidationError


class OrderStatus(str, Enum):
    PENDING = "PENDING"
    PAID = "PAID"
    SHIPPED = "SHIPPED"
    DELIVERED = "DELIVERED"
    CANCELLED = "CANCELLED"
    REFUNDED = "REFUNDED"


# 允许的状态迁移（显式声明，便于状态机可视化与测试）
_ALLOWED_TRANSITIONS: dict[OrderStatus, set[OrderStatus]] = {
    OrderStatus.PENDING: {OrderStatus.PAID, OrderStatus.CANCELLED},
    OrderStatus.PAID: {OrderStatus.SHIPPED, OrderStatus.REFUNDED},
    OrderStatus.SHIPPED: {OrderStatus.DELIVERED, OrderStatus.REFUNDED},
    OrderStatus.DELIVERED: set(),
    OrderStatus.CANCELLED: set(),
    OrderStatus.REFUNDED: set(),
}


@dataclass
class Order:
    order_no: str
    user_id: int
    total_amount: Decimal
    status: OrderStatus = OrderStatus.PENDING
    id: int | None = None
    created_at: datetime = field(default_factory=datetime.utcnow)
    updated_at: datetime = field(default_factory=datetime.utcnow)

    # ---- 工厂 -----------------------------------------------------------
    @classmethod
    def create(cls, order_no: str, user_id: int, total_amount: Decimal | float | str) -> "Order":
        if not order_no:
            raise ValidationError("订单号不能为空")
        if user_id <= 0:
            raise ValidationError("用户 ID 无效")
        amount = Decimal(str(total_amount))
        if amount <= 0:
            raise ValidationError("订单金额必须大于 0")
        now = datetime.utcnow()
        return cls(
            order_no=order_no,
            user_id=user_id,
            total_amount=amount,
            status=OrderStatus.PENDING,
            created_at=now,
            updated_at=now,
        )

    # ---- 行为（全部走 _transition 校验） --------------------------------
    def pay(self) -> None:
        self._transition(OrderStatus.PAID, "只有待支付订单可以支付")

    def ship(self) -> None:
        self._transition(OrderStatus.SHIPPED, "只有已支付订单可以发货")

    def deliver(self) -> None:
        self._transition(OrderStatus.DELIVERED, "只有已发货订单可以确认送达")

    def cancel(self) -> None:
        self._transition(OrderStatus.CANCELLED, "只有待支付订单可以取消")

    def refund(self) -> None:
        self._transition(OrderStatus.REFUNDED, "只有已支付或已发货的订单可以退款")

    # ---- 查询 -----------------------------------------------------------
    def can_cancel(self) -> bool:
        return self.status == OrderStatus.PENDING

    # ---- 内部 -----------------------------------------------------------
    def _transition(self, target: OrderStatus, err_msg: str) -> None:
        if target not in _ALLOWED_TRANSITIONS.get(self.status, set()):
            raise DomainError(err_msg)
        self.status = target
        self.updated_at = datetime.utcnow()

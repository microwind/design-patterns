"""订单应用服务。"""
from __future__ import annotations

from datetime import datetime
from decimal import Decimal

from shared.domain.publisher import EventPublisher
from shared.infrastructure.events import get_publisher
from shared.infrastructure.exceptions import NotFoundError
from order.application.dto import CreateOrderCommand, OrderDTO
from order.domain.events import (
    OrderCancelledEvent,
    OrderCreatedEvent,
    OrderDeliveredEvent,
    OrderPaidEvent,
    OrderRefundedEvent,
    OrderShippedEvent,
)
from order.domain.order import Order
from order.domain.repository import OrderRepository

_COUNTER = 0  # 单进程简单序列，生产请换成雪花 / DB 序列


def _generate_order_no() -> str:
    global _COUNTER
    _COUNTER += 1
    return f"ORD{datetime.utcnow():%Y%m%d%H%M%S}{_COUNTER:04d}"


class OrderApplicationService:
    def __init__(
        self,
        repository: OrderRepository,
        publisher: EventPublisher | None = None,
    ) -> None:
        self._repo = repository
        self._publisher = publisher or get_publisher()

    # ---- 创建 ----------------------------------------------------------
    def create_order(self, cmd: CreateOrderCommand) -> OrderDTO:
        order_no = cmd.order_no or _generate_order_no()
        order = Order.create(order_no=order_no, user_id=cmd.user_id, total_amount=cmd.total_amount)
        order = self._repo.save(order)
        assert order.id is not None
        self._publisher.publish(
            OrderCreatedEvent(
                order_id=order.id,
                order_no=order.order_no,
                user_id=order.user_id,
                total_amount=order.total_amount,
            )
        )
        return _to_dto(order)

    # ---- 状态迁移 ------------------------------------------------------
    def pay(self, order_id: int) -> OrderDTO:
        return self._transition(order_id, "pay", OrderPaidEvent)

    def ship(self, order_id: int) -> OrderDTO:
        return self._transition(order_id, "ship", OrderShippedEvent)

    def deliver(self, order_id: int) -> OrderDTO:
        return self._transition(order_id, "deliver", OrderDeliveredEvent)

    def cancel(self, order_id: int) -> OrderDTO:
        return self._transition(order_id, "cancel", OrderCancelledEvent)

    def refund(self, order_id: int) -> OrderDTO:
        return self._transition(order_id, "refund", OrderRefundedEvent)

    # ---- 查询 -----------------------------------------------------------
    def get_order(self, order_id: int) -> OrderDTO:
        return _to_dto(self._require(order_id))

    def list_orders(self, offset: int = 0, limit: int = 20) -> tuple[list[OrderDTO], int]:
        orders, total = self._repo.list_all(offset=offset, limit=limit)
        return [_to_dto(o) for o in orders], total

    def list_user_orders(
        self, user_id: int, offset: int = 0, limit: int = 20
    ) -> tuple[list[OrderDTO], int]:
        orders, total = self._repo.list_by_user(user_id=user_id, offset=offset, limit=limit)
        return [_to_dto(o) for o in orders], total

    # ---- helper ---------------------------------------------------------
    def _transition(self, order_id: int, action: str, event_cls) -> OrderDTO:
        order = self._require(order_id)
        getattr(order, action)()
        order = self._repo.save(order)
        assert order.id is not None
        self._publisher.publish(event_cls(order_id=order.id, order_no=order.order_no))
        return _to_dto(order)

    def _require(self, order_id: int) -> Order:
        order = self._repo.find_by_id(order_id)
        if order is None:
            raise NotFoundError(f"订单不存在: id={order_id}")
        return order


def _to_dto(order: Order) -> OrderDTO:
    assert order.id is not None
    return OrderDTO(
        id=order.id,
        order_no=order.order_no,
        user_id=order.user_id,
        total_amount=Decimal(str(order.total_amount)),
        status=order.status.value,
        created_at=order.created_at,
        updated_at=order.updated_at,
    )

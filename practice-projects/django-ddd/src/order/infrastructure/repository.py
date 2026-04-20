"""OrderRepository 的 Django ORM 实现。"""
from __future__ import annotations

from decimal import Decimal

from order.domain.order import Order, OrderStatus
from order.domain.repository import OrderRepository
from order.infrastructure.models import OrderModel


def _to_domain(row: OrderModel) -> Order:
    return Order(
        id=row.pk,
        order_no=row.order_no,
        user_id=row.user_id,
        total_amount=Decimal(row.total_amount),
        status=OrderStatus(row.status),
        created_at=row.created_at,
        updated_at=row.updated_at,
    )


class DjangoOrderRepository(OrderRepository):
    def save(self, order: Order) -> Order:
        if order.id is None:
            row = OrderModel.objects.create(
                order_no=order.order_no,
                user_id=order.user_id,
                total_amount=order.total_amount,
                status=order.status.value,
            )
        else:
            row = OrderModel.objects.get(pk=order.id)
            row.order_no = order.order_no
            row.user_id = order.user_id
            row.total_amount = order.total_amount
            row.status = order.status.value
            row.save()
        return _to_domain(row)

    def find_by_id(self, order_id: int) -> Order | None:
        row = OrderModel.objects.filter(pk=order_id).first()
        return _to_domain(row) if row else None

    def list_all(self, offset: int = 0, limit: int = 20) -> tuple[list[Order], int]:
        qs = OrderModel.objects.all().order_by("-id")
        total = qs.count()
        rows = list(qs[offset : offset + limit])
        return [_to_domain(r) for r in rows], total

    def list_by_user(
        self, user_id: int, offset: int = 0, limit: int = 20
    ) -> tuple[list[Order], int]:
        qs = OrderModel.objects.filter(user_id=user_id).order_by("-id")
        total = qs.count()
        rows = list(qs[offset : offset + limit])
        return [_to_domain(r) for r in rows], total

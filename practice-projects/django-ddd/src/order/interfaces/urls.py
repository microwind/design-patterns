"""订单上下文 URL。"""
from __future__ import annotations

from django.urls import path

from order.interfaces.views import (
    OrderCancelView,
    OrderDeliverView,
    OrderDetailView,
    OrderListView,
    OrderPayView,
    OrderRefundView,
    OrderShipView,
)

urlpatterns = [
    path("orders", OrderListView.as_view(), name="orders"),
    path("orders/<int:order_id>", OrderDetailView.as_view(), name="order-detail"),
    path("orders/<int:order_id>/pay", OrderPayView.as_view(), name="order-pay"),
    path("orders/<int:order_id>/ship", OrderShipView.as_view(), name="order-ship"),
    path("orders/<int:order_id>/deliver", OrderDeliverView.as_view(), name="order-deliver"),
    path("orders/<int:order_id>/cancel", OrderCancelView.as_view(), name="order-cancel"),
    path("orders/<int:order_id>/refund", OrderRefundView.as_view(), name="order-refund"),
]

"""订单上下文的事件监听器。

默认仅打印日志。生产可在这里接入消息队列 / 物流回调 / 财务对账等。
"""
from __future__ import annotations

import logging

from order.domain.events import (
    OrderCancelledEvent,
    OrderCreatedEvent,
    OrderDeliveredEvent,
    OrderPaidEvent,
    OrderRefundedEvent,
    OrderShippedEvent,
)
from shared.domain.publisher import EventPublisher

logger = logging.getLogger("ddd.events.order")


def register_listeners(publisher: EventPublisher) -> None:
    publisher.subscribe(OrderCreatedEvent, lambda e: logger.info("订单创建: %s", e))
    publisher.subscribe(OrderPaidEvent, lambda e: logger.info("订单支付: %s", e))
    publisher.subscribe(OrderShippedEvent, lambda e: logger.info("订单发货: %s", e))
    publisher.subscribe(OrderDeliveredEvent, lambda e: logger.info("订单送达: %s", e))
    publisher.subscribe(OrderCancelledEvent, lambda e: logger.info("订单取消: %s", e))
    publisher.subscribe(OrderRefundedEvent, lambda e: logger.info("订单退款: %s", e))

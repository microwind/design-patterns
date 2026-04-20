from __future__ import annotations

from django.apps import AppConfig


class OrderConfig(AppConfig):
    name = "order"
    label = "order"
    default_auto_field = "django.db.models.BigAutoField"
    verbose_name = "订单上下文"

    def ready(self) -> None:
        from order.infrastructure.listeners import register_listeners
        from shared.infrastructure.events import get_publisher

        register_listeners(get_publisher())

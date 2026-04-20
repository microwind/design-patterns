from __future__ import annotations

from django.apps import AppConfig


class UserConfig(AppConfig):
    name = "user"
    label = "user"
    default_auto_field = "django.db.models.BigAutoField"
    verbose_name = "用户上下文"

    def ready(self) -> None:
        from shared.infrastructure.events import get_publisher
        from user.infrastructure.listeners import register_listeners

        register_listeners(get_publisher())

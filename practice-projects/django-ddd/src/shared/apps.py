"""共享层 AppConfig：启动时初始化事件发布器（抽象）。

各 BC（user / order）在自己的 AppConfig.ready() 里注册各自的监听器，
从而保持 shared 层完全不依赖任何业务上下文。
"""
from __future__ import annotations

from django.apps import AppConfig


class SharedConfig(AppConfig):
    name = "shared"
    verbose_name = "shared-infrastructure"

    def ready(self) -> None:
        from django.conf import settings

        from shared.infrastructure import events as _events

        kind = getattr(settings, "DDD_EVENT_PUBLISHER_KIND", "memory")
        _events.configure_publisher(kind)

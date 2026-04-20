"""数据库路由器：按 app_label 把 ORM 模型分发到不同数据库。

- user   → default（MySQL）
- order  → order_db（PostgreSQL）
- 其他内建 app（contenttypes / auth）→ default
"""
from __future__ import annotations

APP_DB_MAPPING = {
    "user": "default",
    "order": "order_db",
}


class AppLabelRouter:
    """根据 app_label 路由读写与迁移。"""

    def _db_for(self, model) -> str:
        return APP_DB_MAPPING.get(model._meta.app_label, "default")

    def db_for_read(self, model, **hints):  # noqa: D401, ARG002
        return self._db_for(model)

    def db_for_write(self, model, **hints):  # noqa: ARG002
        return self._db_for(model)

    def allow_relation(self, obj1, obj2, **hints):  # noqa: ARG002
        # 允许同库内的关系；跨库关系交给上层避免
        return APP_DB_MAPPING.get(obj1._meta.app_label) == APP_DB_MAPPING.get(obj2._meta.app_label) or None

    def allow_migrate(self, db, app_label, model_name=None, **hints):  # noqa: ARG002
        target = APP_DB_MAPPING.get(app_label, "default")
        return db == target

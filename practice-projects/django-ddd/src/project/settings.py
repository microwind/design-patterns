"""Django settings for django-ddd.

配置来自 config/config.yaml（可通过环境变量 CONFIG_FILE 切换），
Django 自身需要的 SECRET_KEY 等读 .env / 环境变量。
"""
from __future__ import annotations

import os
from pathlib import Path

from shared.infrastructure.config import load_config

BASE_DIR = Path(__file__).resolve().parent.parent.parent
cfg = load_config()

# -----------------------------------------------------------------------------
# 基础
# -----------------------------------------------------------------------------
SECRET_KEY = os.environ.get("DJANGO_SECRET_KEY", "dev-insecure-secret-key-change-me")
DEBUG = bool(cfg.server.debug) if cfg.server.debug is not None else (
    os.environ.get("DJANGO_DEBUG", "True").lower() == "true"
)
ALLOWED_HOSTS = ["*"]

# -----------------------------------------------------------------------------
# 已安装的应用（每个 bounded context = 一个 Django app）
# -----------------------------------------------------------------------------
INSTALLED_APPS = [
    "rest_framework",
    "drf_spectacular",

    "shared.apps.SharedConfig",
    "user.apps.UserConfig",
    "order.apps.OrderConfig",
]

MIDDLEWARE = [
    "django.middleware.common.CommonMiddleware",
]

ROOT_URLCONF = "project.urls"
WSGI_APPLICATION = "project.wsgi.application"
ASGI_APPLICATION = "project.asgi.application"

# drf-spectacular 的 Swagger UI / Redoc 渲染需要 Django 模板后端，
# 打开 APP_DIRS 即可从第三方包（如 drf_spectacular/templates/）加载模板。
TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [],
        "APP_DIRS": True,
        "OPTIONS": {},
    },
]

# -----------------------------------------------------------------------------
# 双数据库：user → MySQL（default），order → PostgreSQL（order_db）
# -----------------------------------------------------------------------------
def _build_db(db_cfg) -> dict:
    engine = {
        "mysql": "django.db.backends.mysql",
        "postgres": "django.db.backends.postgresql",
    }.get(db_cfg.engine, "django.db.backends.sqlite3")
    return {
        "ENGINE": engine,
        "NAME": db_cfg.name,
        "USER": db_cfg.username,
        "PASSWORD": db_cfg.password,
        "HOST": db_cfg.host,
        "PORT": str(db_cfg.port),
        "CONN_MAX_AGE": db_cfg.conn_max_age,
        "OPTIONS": (
            {"charset": "utf8mb4"} if db_cfg.engine == "mysql" else {}
        ),
    }


DATABASES = {
    "default": _build_db(cfg.database.user),
    "order_db": _build_db(cfg.database.order),
}

DATABASE_ROUTERS = ["project.routers.AppLabelRouter"]

# -----------------------------------------------------------------------------
# DRF
# -----------------------------------------------------------------------------
REST_FRAMEWORK = {
    "DEFAULT_SCHEMA_CLASS": "drf_spectacular.openapi.AutoSchema",
    "EXCEPTION_HANDLER": "shared.infrastructure.exceptions.global_exception_handler",
    "DEFAULT_RENDERER_CLASSES": (
        "rest_framework.renderers.JSONRenderer",
    ),
    "UNAUTHENTICATED_USER": None,
}

SPECTACULAR_SETTINGS = {
    "TITLE": "django-ddd API",
    "DESCRIPTION": "和 gin-ddd / nestjs-ddd 对齐的 Django DDD 脚手架",
    "VERSION": "1.0.0",
    "SERVE_INCLUDE_SCHEMA": False,
}

# -----------------------------------------------------------------------------
# 国际化 & 默认主键
# -----------------------------------------------------------------------------
LANGUAGE_CODE = "zh-hans"
TIME_ZONE = "Asia/Shanghai"
USE_I18N = True
USE_TZ = True
DEFAULT_AUTO_FIELD = "django.db.models.BigAutoField"

# -----------------------------------------------------------------------------
# 日志
# -----------------------------------------------------------------------------
LOGGING = {
    "version": 1,
    "disable_existing_loggers": False,
    "formatters": {
        "std": {"format": "[%(asctime)s] %(levelname)s %(name)s - %(message)s"},
    },
    "handlers": {
        "console": {
            "class": "logging.StreamHandler",
            "formatter": "std",
        },
    },
    "root": {"handlers": ["console"], "level": cfg.logger.level},
    "loggers": {
        "django": {"handlers": ["console"], "level": "INFO", "propagate": False},
    },
}

# 对外暴露配置对象，应用层可注入使用
APP_CONFIG = cfg

# -----------------------------------------------------------------------------
# 运行时绑定：启动时自动注册事件监听器
# -----------------------------------------------------------------------------
DDD_EVENT_PUBLISHER_KIND = cfg.event.publisher

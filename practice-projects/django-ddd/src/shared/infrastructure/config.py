"""YAML 配置加载。

目标：
- 读取 config/config.yaml（可用 CONFIG_FILE 覆盖）
- 映射为强类型的 dataclass，应用内部直接点号访问
- 缺省值友好，便于本地开发
"""
from __future__ import annotations

import os
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any

import yaml

PROJECT_ROOT = Path(__file__).resolve().parents[3]
DEFAULT_CONFIG = PROJECT_ROOT / "config" / "config.yaml"


@dataclass
class ServerConfig:
    host: str = "0.0.0.0"
    port: int = 8080
    debug: bool = True


@dataclass
class DatabaseEntry:
    engine: str = "mysql"
    host: str = "localhost"
    port: int = 3306
    name: str = ""
    username: str = ""
    password: str = ""
    conn_max_age: int = 60


@dataclass
class DatabaseConfig:
    user: DatabaseEntry = field(default_factory=DatabaseEntry)
    order: DatabaseEntry = field(default_factory=DatabaseEntry)


@dataclass
class LoggerConfig:
    level: str = "INFO"


@dataclass
class EventConfig:
    publisher: str = "memory"


@dataclass
class AppConfig:
    server: ServerConfig = field(default_factory=ServerConfig)
    database: DatabaseConfig = field(default_factory=DatabaseConfig)
    logger: LoggerConfig = field(default_factory=LoggerConfig)
    event: EventConfig = field(default_factory=EventConfig)


def _coerce(raw: dict[str, Any] | None) -> AppConfig:
    raw = raw or {}

    server = ServerConfig(**(raw.get("server") or {}))

    db_raw = raw.get("database") or {}
    database = DatabaseConfig(
        user=DatabaseEntry(**(db_raw.get("user") or {})),
        order=DatabaseEntry(**(db_raw.get("order") or {})),
    )

    logger = LoggerConfig(**(raw.get("logger") or {}))
    event = EventConfig(**(raw.get("event") or {}))

    return AppConfig(server=server, database=database, logger=logger, event=event)


def load_config(path: str | os.PathLike | None = None) -> AppConfig:
    """加载 YAML 并返回 AppConfig。优先级：传参 > 环境变量 > 默认路径。"""
    cfg_path = Path(path or os.environ.get("CONFIG_FILE") or DEFAULT_CONFIG)
    if not cfg_path.exists():
        # 文件缺失时返回默认值，保证 Django 能起来，再按需报错
        return _coerce(None)

    with cfg_path.open("r", encoding="utf-8") as fp:
        data = yaml.safe_load(fp) or {}

    return _coerce(data)

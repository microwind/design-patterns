from dataclasses import dataclass
from typing import Dict, Optional, Tuple


@dataclass
class ServiceConfig:
    service_name: str
    environment: str
    version: int
    db_host: str
    timeout_ms: int
    feature_order_audit: bool


class ConfigCenter:
    def __init__(self) -> None:
        self._store: Dict[Tuple[str, str], ServiceConfig] = {}

    def put(self, config: ServiceConfig) -> None:
        self._store[(config.service_name, config.environment)] = config

    def get(self, service_name: str, environment: str) -> Optional[ServiceConfig]:
        return self._store.get((service_name, environment))


class ConfigClient:
    def __init__(self, center: ConfigCenter, service_name: str, environment: str) -> None:
        self._center = center
        self._service_name = service_name
        self._environment = environment
        self._current: Optional[ServiceConfig] = None

    def load(self) -> Optional[ServiceConfig]:
        config = self._center.get(self._service_name, self._environment)
        self._current = config
        return config

    def refresh(self) -> Optional[ServiceConfig]:
        return self.load()

    def current(self) -> Optional[ServiceConfig]:
        return self._current

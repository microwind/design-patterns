"""
router.py - API 版本管理模式（API Versioning Pattern）的 Python 实现

支持 URL 路径版本（/v1/、/v2/）和 Header 版本（X-API-Version），提供默认版本兜底。

【设计模式】
  - 策略模式（Strategy Pattern）：不同版本的处理器是可互换的策略，
    路由器根据解析到的版本号选择对应策略执行。
  - 工厂方法模式（Factory Method）：resolve_version 根据请求上下文决定使用哪个版本。
  - 模板方法模式（Template Method）：handle 定义了"解析版本 → 查找处理器 → 执行"的骨架。

【架构思想】
  API 版本管理让新老客户端并行使用不同版本接口，实现平滑演进。

【开源对比】
  - Django REST Framework：URLPathVersioning / HeaderVersioning / QueryParameterVersioning
  - FastAPI：通过路由前缀或 APIRouter 实现版本管理
  本示例省略了版本协商和废弃通知等工程细节，聚焦于版本解析和路由分发。
"""

from dataclasses import dataclass
from typing import Callable, Dict


@dataclass
class Request:
    path: str
    headers: Dict[str, str]


@dataclass
class Response:
    status_code: int
    version: str
    body: str


class VersionedRouter:
    def __init__(self, default_version: str) -> None:
        self._default_version = self._normalize(default_version)
        self._handlers: Dict[str, Callable[[], str]] = {}

    def register(self, version: str, handler: Callable[[], str]) -> None:
        self._handlers[self._normalize(version)] = handler

    def handle(self, request: Request) -> Response:
        version = self.resolve_version(request)
        handler = self._handlers.get(version)
        if handler is None:
            return Response(400, version, "unsupported api version")
        return Response(200, version, handler())

    def resolve_version(self, request: Request) -> str:
        path = request.path.lower()
        if "/v2/" in path:
            return "v2"
        if "/v1/" in path:
            return "v1"
        header_version = self._normalize(request.headers.get("X-API-Version", ""))
        if header_version:
            return header_version
        return self._default_version

    @staticmethod
    def _normalize(version: str) -> str:
        version = version.strip().lower()
        if not version:
            return ""
        return version if version.startswith("v") else f"v{version}"


def product_handler_v1() -> str:
    return '{"id":"P100","name":"Mechanical Keyboard"}'


def product_handler_v2() -> str:
    return '{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}'

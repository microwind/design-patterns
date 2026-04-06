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

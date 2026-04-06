import threading
import time
from dataclasses import dataclass
from typing import Callable, Dict, List, Optional, Tuple


class TimeoutError(Exception):
    pass


class CircuitOpenError(Exception):
    pass


@dataclass
class Result:
    value: str = ""
    error: Optional[Exception] = None
    delay_seconds: float = 0.0


class ScriptedDependency:
    def __init__(self, results: List[Result]) -> None:
        self._results = list(results)
        self._index = 0

    def call(self) -> str:
        if not self._results:
            raise RuntimeError("no scripted result available")

        index = self._index if self._index < len(self._results) else len(self._results) - 1
        result = self._results[index]
        self._index += 1

        if result.delay_seconds > 0:
            time.sleep(result.delay_seconds)
        if result.error is not None:
            raise result.error
        return result.value


def call_with_timeout(timeout_seconds: float, operation: Callable[[], str]) -> str:
    container: Dict[str, object] = {}

    def worker() -> None:
        try:
            container["value"] = operation()
        except Exception as exc:  # pragma: no cover - exercised by callers
            container["error"] = exc

    thread = threading.Thread(target=worker, daemon=True)
    thread.start()
    thread.join(timeout_seconds)
    if thread.is_alive():
        raise TimeoutError("operation timed out")
    if "error" in container:
        raise container["error"]  # type: ignore[misc]
    return str(container["value"])


def retry(max_attempts: int, operation: Callable[[], str]) -> Tuple[str, int]:
    max_attempts = max(1, max_attempts)
    last_error: Optional[Exception] = None

    for attempt in range(1, max_attempts + 1):
        try:
            return operation(), attempt
        except Exception as exc:
            last_error = exc

    assert last_error is not None
    raise last_error


class CircuitBreaker:
    def __init__(self, failure_threshold: int) -> None:
        self._failure_threshold = max(1, failure_threshold)
        self._consecutive_failures = 0
        self._open = False

    def execute(self, operation: Callable[[], str], fallback: str) -> str:
        if self._open:
            raise CircuitOpenError(fallback)

        try:
            value = operation()
            self._consecutive_failures = 0
            return value
        except Exception:
            self._consecutive_failures += 1
            if self._consecutive_failures >= self._failure_threshold:
                self._open = True
            return fallback

    def reset(self) -> None:
        self._open = False
        self._consecutive_failures = 0

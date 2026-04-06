from dataclasses import dataclass
from typing import Dict, List


@dataclass
class Backend:
    backend_id: str
    weight: int = 1
    active_connections: int = 0


class RoundRobinBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._backends = list(backends)
        self._next = 0

    def next(self) -> Backend:
        backend = self._backends[self._next % len(self._backends)]
        self._next += 1
        return backend


class WeightedRoundRobinBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._sequence: List[Backend] = []
        for backend in backends:
            repeat = backend.weight if backend.weight > 0 else 1
            self._sequence.extend([backend] * repeat)
        self._next = 0

    def next(self) -> Backend:
        backend = self._sequence[self._next % len(self._sequence)]
        self._next += 1
        return backend


class LeastConnectionsBalancer:
    def __init__(self, backends: List[Backend]) -> None:
        self._backends: Dict[str, Backend] = {
            backend.backend_id: Backend(
                backend_id=backend.backend_id,
                weight=backend.weight,
                active_connections=backend.active_connections,
            )
            for backend in backends
        }

    def acquire(self) -> Backend:
        backend = min(self._backends.values(), key=lambda item: item.active_connections)
        backend.active_connections += 1
        return backend

    def release(self, backend_id: str) -> None:
        backend = self._backends.get(backend_id)
        if backend and backend.active_connections > 0:
            backend.active_connections -= 1

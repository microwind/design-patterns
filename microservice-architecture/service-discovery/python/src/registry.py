from dataclasses import dataclass
from typing import Dict, List, Optional


@dataclass(frozen=True)
class ServiceInstance:
    instance_id: str
    address: str


class ServiceRegistry:
    def __init__(self) -> None:
        self._services: Dict[str, Dict[str, ServiceInstance]] = {}

    def register(self, service_name: str, instance: ServiceInstance) -> None:
        self._services.setdefault(service_name, {})
        self._services[service_name][instance.instance_id] = instance

    def deregister(self, service_name: str, instance_id: str) -> bool:
        instances = self._services.get(service_name)
        if not instances or instance_id not in instances:
            return False
        del instances[instance_id]
        return True

    def instances(self, service_name: str) -> List[ServiceInstance]:
        instances = list(self._services.get(service_name, {}).values())
        return sorted(instances, key=lambda item: item.instance_id)


class RoundRobinDiscoverer:
    def __init__(self, registry: ServiceRegistry) -> None:
        self._registry = registry
        self._offsets: Dict[str, int] = {}

    def next(self, service_name: str) -> Optional[ServiceInstance]:
        instances = self._registry.instances(service_name)
        if not instances:
            return None
        index = self._offsets.get(service_name, 0) % len(instances)
        self._offsets[service_name] = self._offsets.get(service_name, 0) + 1
        return instances[index]

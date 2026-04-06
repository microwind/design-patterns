import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.registry import RoundRobinDiscoverer, ServiceInstance, ServiceRegistry  # noqa: E402


class ServiceDiscoveryTest(unittest.TestCase):
    def test_registry_returns_instances(self) -> None:
        registry = ServiceRegistry()
        registry.register("inventory-service", ServiceInstance("inventory-a", "10.0.0.1:8081"))
        registry.register("inventory-service", ServiceInstance("inventory-b", "10.0.0.2:8081"))

        instances = registry.instances("inventory-service")
        self.assertEqual(2, len(instances))

    def test_round_robin_cycles_instances(self) -> None:
        registry = ServiceRegistry()
        registry.register("inventory-service", ServiceInstance("inventory-a", "10.0.0.1:8081"))
        registry.register("inventory-service", ServiceInstance("inventory-b", "10.0.0.2:8081"))

        discoverer = RoundRobinDiscoverer(registry)
        self.assertEqual("inventory-a", discoverer.next("inventory-service").instance_id)
        self.assertEqual("inventory-b", discoverer.next("inventory-service").instance_id)
        self.assertEqual("inventory-a", discoverer.next("inventory-service").instance_id)

    def test_registry_can_deregister(self) -> None:
        registry = ServiceRegistry()
        registry.register("inventory-service", ServiceInstance("inventory-a", "10.0.0.1:8081"))
        registry.register("inventory-service", ServiceInstance("inventory-b", "10.0.0.2:8081"))

        self.assertTrue(registry.deregister("inventory-service", "inventory-a"))
        instances = registry.instances("inventory-service")

        self.assertEqual(1, len(instances))
        self.assertEqual("inventory-b", instances[0].instance_id)


if __name__ == "__main__":
    unittest.main()

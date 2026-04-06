import os
import sys
import unittest
from collections import Counter

sys.path.append(os.getcwd())

from src.balancer import (  # noqa: E402
    Backend,
    LeastConnectionsBalancer,
    RoundRobinBalancer,
    WeightedRoundRobinBalancer,
)


class LoadBalancingTest(unittest.TestCase):
    def test_round_robin(self) -> None:
        balancer = RoundRobinBalancer(
            [Backend("node-a"), Backend("node-b"), Backend("node-c")]
        )

        self.assertEqual("node-a", balancer.next().backend_id)
        self.assertEqual("node-b", balancer.next().backend_id)
        self.assertEqual("node-c", balancer.next().backend_id)

    def test_weighted_round_robin(self) -> None:
        balancer = WeightedRoundRobinBalancer(
            [Backend("node-a", weight=2), Backend("node-b", weight=1)]
        )
        counts = Counter(balancer.next().backend_id for _ in range(6))
        self.assertEqual(4, counts["node-a"])
        self.assertEqual(2, counts["node-b"])

    def test_least_connections(self) -> None:
        balancer = LeastConnectionsBalancer(
            [
                Backend("node-a", active_connections=2),
                Backend("node-b", active_connections=0),
                Backend("node-c", active_connections=1),
            ]
        )
        first = balancer.acquire()
        self.assertEqual("node-b", first.backend_id)
        balancer.release("node-b")


if __name__ == "__main__":
    unittest.main()

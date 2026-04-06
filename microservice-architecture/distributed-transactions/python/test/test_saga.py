import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.saga import SagaCoordinator  # noqa: E402


class DistributedTransactionsTest(unittest.TestCase):
    def test_saga_success(self) -> None:
        coordinator = SagaCoordinator(10, False)
        order = coordinator.execute("ORD-1001", "SKU-BOOK", 2)
        self.assertEqual("COMPLETED", order.status)
        self.assertEqual(8, coordinator.inventory.book_stock)

    def test_saga_compensation(self) -> None:
        coordinator = SagaCoordinator(10, True)
        order = coordinator.execute("ORD-1002", "SKU-BOOK", 2)
        self.assertEqual("CANCELLED", order.status)
        self.assertEqual(10, coordinator.inventory.book_stock)


if __name__ == "__main__":
    unittest.main()

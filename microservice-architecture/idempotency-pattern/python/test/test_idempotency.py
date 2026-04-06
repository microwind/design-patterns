import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.idempotency import IdempotencyOrderService  # noqa: E402


class IdempotencyPatternTest(unittest.TestCase):
    def test_idempotency_pattern(self) -> None:
        service = IdempotencyOrderService()

        first = service.create_order("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1)
        self.assertEqual("CREATED", first.status)
        self.assertFalse(first.replayed)

        second = service.create_order("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1)
        self.assertEqual("CREATED", second.status)
        self.assertTrue(second.replayed)

        conflict = service.create_order("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 2)
        self.assertEqual("CONFLICT", conflict.status)
        self.assertFalse(conflict.replayed)


if __name__ == "__main__":
    unittest.main()

import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.limiter import FixedWindowLimiter  # noqa: E402


class RateLimitingTest(unittest.TestCase):
    def test_fixed_window_limiter(self) -> None:
        limiter = FixedWindowLimiter(3)
        self.assertTrue(limiter.allow())
        self.assertTrue(limiter.allow())
        self.assertTrue(limiter.allow())
        self.assertFalse(limiter.allow())
        limiter.advance_window()
        self.assertTrue(limiter.allow())


if __name__ == "__main__":
    unittest.main()

import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.breaker import CircuitBreaker  # noqa: E402


class CircuitBreakerTest(unittest.TestCase):
    def test_state_machine(self) -> None:
        breaker = CircuitBreaker(2)
        self.assertEqual("closed", breaker.state)
        breaker.record_failure()
        breaker.record_failure()
        self.assertEqual("open", breaker.state)
        breaker.probe(True)
        self.assertEqual("closed", breaker.state)
        breaker.record_failure()
        breaker.record_failure()
        breaker.probe(False)
        self.assertEqual("open", breaker.state)


if __name__ == "__main__":
    unittest.main()

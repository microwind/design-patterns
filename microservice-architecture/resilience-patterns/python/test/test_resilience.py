import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.resilience import (  # noqa: E402
    CircuitBreaker,
    CircuitOpenError,
    Result,
    ScriptedDependency,
    TimeoutError,
    call_with_timeout,
    retry,
)


class ResilienceTest(unittest.TestCase):
    def test_retry_eventually_succeeds(self) -> None:
        dependency = ScriptedDependency(
            [
                Result(error=RuntimeError("temporary failure")),
                Result(error=RuntimeError("temporary failure")),
                Result(value="OK"),
            ]
        )

        value, attempts = retry(3, dependency.call)
        self.assertEqual("OK", value)
        self.assertEqual(3, attempts)

    def test_timeout_fails_slow_dependency(self) -> None:
        dependency = ScriptedDependency([Result(value="SLOW_OK", delay_seconds=0.1)])
        with self.assertRaises(TimeoutError):
            call_with_timeout(0.01, dependency.call)

    def test_circuit_breaker_opens_after_repeated_failures(self) -> None:
        breaker = CircuitBreaker(2)
        dependency = ScriptedDependency(
            [
                Result(error=RuntimeError("dependency down")),
                Result(error=RuntimeError("dependency still down")),
                Result(value="RECOVERED"),
            ]
        )

        self.assertEqual("FALLBACK", breaker.execute(dependency.call, "FALLBACK"))
        self.assertEqual("FALLBACK", breaker.execute(dependency.call, "FALLBACK"))
        with self.assertRaises(CircuitOpenError):
            breaker.execute(dependency.call, "FALLBACK")

        breaker.reset()
        self.assertEqual("RECOVERED", breaker.execute(dependency.call, "FALLBACK"))


if __name__ == "__main__":
    unittest.main()

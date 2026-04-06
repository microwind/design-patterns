import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.retry import ScriptedOperation, retry  # noqa: E402


class RetryPatternTest(unittest.TestCase):
    def test_retry_succeeds(self) -> None:
        ok, attempts = retry(3, ScriptedOperation(2).call)
        self.assertTrue(ok)
        self.assertEqual(3, attempts)

    def test_retry_fails_after_max_attempts(self) -> None:
        ok, attempts = retry(3, ScriptedOperation(5).call)
        self.assertFalse(ok)
        self.assertEqual(3, attempts)


if __name__ == "__main__":
    unittest.main()

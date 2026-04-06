import os
import sys
import unittest

sys.path.append(os.getcwd())

from src.feature_flag import FeatureFlag, FeatureFlagService  # noqa: E402


class FeatureFlagTest(unittest.TestCase):
    def test_feature_flag(self) -> None:
        service = FeatureFlagService()
        service.set("new-checkout", FeatureFlag(False, {"user-1": True}))
        self.assertTrue(service.enabled("new-checkout", "user-1"))
        self.assertFalse(service.enabled("new-checkout", "user-2"))


if __name__ == "__main__":
    unittest.main()

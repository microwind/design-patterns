package src;

import java.util.HashMap;
import java.util.Map;

public class FeatureFlagPattern {

    public static class FeatureFlag {
        private final boolean defaultEnabled;
        private final Map<String, Boolean> allowlist;

        public FeatureFlag(boolean defaultEnabled, Map<String, Boolean> allowlist) {
            this.defaultEnabled = defaultEnabled;
            this.allowlist = allowlist;
        }
    }

    public static class FeatureFlagService {
        private final Map<String, FeatureFlag> flags = new HashMap<>();

        public void set(String flag, FeatureFlag config) {
            flags.put(flag, config);
        }

        public boolean enabled(String flag, String userId) {
            FeatureFlag config = flags.get(flag);
            if (config == null) {
                return false;
            }
            if (config.allowlist.getOrDefault(userId, false)) {
                return true;
            }
            return config.defaultEnabled;
        }
    }
}

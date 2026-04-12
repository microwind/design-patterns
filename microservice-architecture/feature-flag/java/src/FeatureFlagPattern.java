package src;

import java.util.HashMap;
import java.util.Map;

/**
 * FeatureFlagPattern - 特性开关模式的 Java 实现
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：不同开关配置代表不同发布策略（全量/灰度/关闭）。
 *   - 观察者模式（Observer Pattern）：实际工程中开关变更会实时推送到客户端。
 *
 * 【架构思想】
 *   特性开关实现"功能发布与代码发布解耦"，支持灰度发布和快速回滚。
 *
 * 【开源对比】
 *   - LaunchDarkly：SaaS 特性开关平台，支持 streaming 实时推送
 *   - Unleash：开源特性开关系统，支持多种策略
 *   - Flagsmith：开源特性开关 + 远程配置
 *   本示例用内存 Map + 白名单简化。
 */
public class FeatureFlagPattern {

    /**
     * FeatureFlag - 开关配置
     * defaultEnabled：默认是否启用
     * allowlist：按用户定向启用的白名单
     */
    public static class FeatureFlag {
        /** 默认是否启用（全量开/关） */
        private final boolean defaultEnabled;
        /** 白名单：userId -> true 表示该用户启用 */
        private final Map<String, Boolean> allowlist;

        public FeatureFlag(boolean defaultEnabled, Map<String, Boolean> allowlist) {
            this.defaultEnabled = defaultEnabled;
            this.allowlist = allowlist;
        }
    }

    /**
     * FeatureFlagService - 特性开关服务
     * 管理多个开关的注册和评估。
     */
    public static class FeatureFlagService {
        /** 开关注册表：flagName -> FeatureFlag */
        private final Map<String, FeatureFlag> flags = new HashMap<>();

        /** 注册或更新开关配置 */
        public void set(String flag, FeatureFlag config) {
            flags.put(flag, config);
        }

        /**
         * 评估开关是否对指定用户启用。
         * 判断顺序：白名单优先 → 默认值兜底 → 未注册返回 false。
         *
         * @param flag   开关名称
         * @param userId 用户ID
         * @return true=启用，false=禁用
         */
        public boolean enabled(String flag, String userId) {
            FeatureFlag config = flags.get(flag);
            // 未注册的开关默认禁用
            if (config == null) {
                return false;
            }
            // 白名单优先：用户在白名单中则启用
            if (config.allowlist.getOrDefault(userId, false)) {
                return true;
            }
            // 兜底：返回默认值
            return config.defaultEnabled;
        }
    }
}

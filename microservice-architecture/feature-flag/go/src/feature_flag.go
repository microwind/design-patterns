// Package src 实现了特性开关模式（Feature Flag Pattern）的核心逻辑。
//
// 【设计模式】
//   - 策略模式：不同开关配置代表不同发布策略。
//   - 观察者模式：实际工程中开关变更会推送通知。
//
// 【架构思想】特性开关实现"功能发布与代码发布解耦"。
//
// 【开源对比】
//   - Unleash Go SDK：开源特性开关客户端
//   本示例用内存 map + 白名单简化。
package src

// FeatureFlag 开关配置。
type FeatureFlag struct {
	DefaultEnabled bool            // 默认是否启用
	Allowlist      map[string]bool // 白名单：userID -> true
}

// FeatureFlagService 特性开关服务。
type FeatureFlagService struct {
	flags map[string]FeatureFlag // 开关注册表
}

// NewFeatureFlagService 创建特性开关服务。
func NewFeatureFlagService() *FeatureFlagService {
	return &FeatureFlagService{flags: map[string]FeatureFlag{}}
}

// Set 注册或更新开关配置。
func (s *FeatureFlagService) Set(flag string, config FeatureFlag) {
	s.flags[flag] = config
}

// Enabled 评估开关是否对指定用户启用。
// 判断顺序：白名单优先 → 默认值兜底 → 未注册返回 false。
func (s *FeatureFlagService) Enabled(flag string, userID string) bool {
	config, ok := s.flags[flag]
	if !ok {
		return false // 未注册的开关默认禁用
	}
	if config.Allowlist[userID] {
		return true // 白名单优先
	}
	return config.DefaultEnabled // 兜底
}

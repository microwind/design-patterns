package src

type FeatureFlag struct {
	DefaultEnabled bool
	Allowlist      map[string]bool
}

type FeatureFlagService struct {
	flags map[string]FeatureFlag
}

func NewFeatureFlagService() *FeatureFlagService {
	return &FeatureFlagService{flags: map[string]FeatureFlag{}}
}

func (s *FeatureFlagService) Set(flag string, config FeatureFlag) {
	s.flags[flag] = config
}

func (s *FeatureFlagService) Enabled(flag string, userID string) bool {
	config, ok := s.flags[flag]
	if !ok {
		return false
	}
	if config.Allowlist[userID] {
		return true
	}
	return config.DefaultEnabled
}

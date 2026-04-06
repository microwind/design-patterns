export type FeatureFlag = {
  defaultEnabled: boolean;
  allowlist: Record<string, boolean>;
};

export class FeatureFlagService {
  private flags: Record<string, FeatureFlag> = {};

  set(flag: string, config: FeatureFlag): void {
    this.flags[flag] = config;
  }

  enabled(flag: string, userId: string): boolean {
    const config = this.flags[flag];
    if (!config) return false;
    if (config.allowlist[userId]) return true;
    return config.defaultEnabled;
  }
}

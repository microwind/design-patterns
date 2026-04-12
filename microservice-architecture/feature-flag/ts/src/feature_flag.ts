/**
 * @file feature_flag.ts - 特性开关模式（Feature Flag Pattern）的 TypeScript 实现
 *
 * 【设计模式】策略模式：不同开关配置代表不同发布策略。
 * 【架构思想】TypeScript 通过 FeatureFlag 类型和 Record 保证类型安全。
 * 【开源对比】unleash-client-node / launchdarkly-node-server-sdk
 */

/** 开关配置类型 */
export type FeatureFlag = {
  defaultEnabled: boolean;           // 默认是否启用
  allowlist: Record<string, boolean>; // 白名单
};

/** FeatureFlagService - 特性开关服务 */
export class FeatureFlagService {
  private flags: Record<string, FeatureFlag> = {};

  /** 注册或更新开关配置 */
  set(flag: string, config: FeatureFlag): void {
    this.flags[flag] = config;
  }

  /** 评估开关是否对指定用户启用。白名单优先 → 默认值兜底。 */
  enabled(flag: string, userId: string): boolean {
    const config = this.flags[flag];
    if (!config) return false;
    if (config.allowlist[userId]) return true;
    return config.defaultEnabled;
  }
}

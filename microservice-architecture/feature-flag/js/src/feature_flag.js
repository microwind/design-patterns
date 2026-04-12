/**
 * @file feature_flag.js - 特性开关模式（Feature Flag Pattern）的 JavaScript 实现
 *
 * 【设计模式】策略模式：不同开关配置代表不同发布策略。
 * 【架构思想】特性开关实现"功能发布与代码发布解耦"。
 * 【开源对比】unleash-client-node / launchdarkly-node-server-sdk
 */

/** FeatureFlagService - 特性开关服务 */
export class FeatureFlagService {
  constructor() {
    /** @type {Map<string, {defaultEnabled: boolean, allowlist: object}>} */
    this.flags = new Map()
  }

  /** 注册或更新开关配置 */
  set(flag, config) {
    this.flags.set(flag, config)
  }

  /**
   * 评估开关是否对指定用户启用。白名单优先 → 默认值兜底。
   * @param {string} flag   开关名称
   * @param {string} userId 用户ID
   * @returns {boolean}
   */
  enabled(flag, userId) {
    const config = this.flags.get(flag)
    if (!config) return false       // 未注册默认禁用
    if (config.allowlist[userId]) return true  // 白名单优先
    return config.defaultEnabled    // 兜底
  }
}

export class FeatureFlagService {
  constructor() {
    this.flags = new Map()
  }

  set(flag, config) {
    this.flags.set(flag, config)
  }

  enabled(flag, userId) {
    const config = this.flags.get(flag)
    if (!config) return false
    if (config.allowlist[userId]) return true
    return config.defaultEnabled
  }
}

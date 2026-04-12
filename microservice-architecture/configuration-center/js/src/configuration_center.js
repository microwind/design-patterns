/**
 * @file configuration_center.js - 配置中心模式（Configuration Center Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 观察者模式：实际工程中配置变更会推送通知，本示例简化为客户端主动 refresh。
 *   - 代理模式：ConfigClient 代理 ConfigCenter 访问并缓存配置。
 *
 * 【架构思想】
 *   配置中心将所有服务配置集中存储，按"服务名+环境"维度管理。
 *
 * 【开源对比】
 *   - node-config：Node.js 本地配置管理（非远程配置中心）
 *   - 实际工程中通常接入 Apollo / Nacos 等远程配置中心
 *   本示例用内存 Map 简化。
 */

/**
 * ConfigCenter - 配置中心服务端
 * 【设计模式】注册表模式：按 "serviceName@environment" 键存储配置。
 */
export class ConfigCenter {
  constructor() {
    /** @type {Map<string, object>} 配置存储 */
    this.store = new Map()
  }

  /**
   * 发布配置。同一 key 重复发布会覆盖（支持更新）。
   * @param {object} config 服务配置对象
   */
  put(config) {
    this.store.set(`${config.serviceName}@${config.environment}`, config)
  }

  /**
   * 获取指定服务和环境的配置。
   * @param {string} serviceName 服务名称
   * @param {string} environment 环境标识
   * @returns {object|null} 配置对象
   */
  get(serviceName, environment) {
    return this.store.get(`${serviceName}@${environment}`) || null
  }
}

/**
 * ConfigClient - 配置客户端
 * 【设计模式】代理模式：代理 ConfigCenter 访问，本地缓存配置快照。
 */
export class ConfigClient {
  /**
   * @param {ConfigCenter} center      配置中心
   * @param {string}       serviceName 绑定的服务名
   * @param {string}       environment 绑定的环境
   */
  constructor(center, serviceName, environment) {
    this.center = center
    this.serviceName = serviceName
    this.environment = environment
    /** @type {object|null} 当前缓存的配置快照 */
    this.currentConfig = null
  }

  /** 首次加载配置 */
  load() {
    this.currentConfig = this.center.get(this.serviceName, this.environment)
    return this.currentConfig
  }

  /** 刷新配置（重新拉取） */
  refresh() {
    return this.load()
  }

  /** 获取当前缓存的配置快照 */
  current() {
    return this.currentConfig
  }
}

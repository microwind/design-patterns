/**
 * @file registry.js - 服务发现模式（Service Discovery Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 注册表模式（Registry Pattern）：ServiceRegistry 维护服务名到实例列表的映射。
 *   - 策略模式（Strategy Pattern）：RoundRobinDiscoverer 封装轮询选择策略。
 *
 * 【架构思想】
 *   服务发现解决微服务架构中"调用方如何找到被调服务"的问题。
 *
 * 【开源对比】
 *   - consul（npm）：Node.js 的 Consul 客户端
 *   本示例用内存 Map 简化，省略了心跳和网络通信。
 */

/**
 * ServiceRegistry - 服务注册中心
 * 【设计模式】注册表模式：维护服务名到实例列表的全局映射。
 */
export class ServiceRegistry {
  constructor() {
    /** @type {Map<string, Map<string, object>>} 服务名 -> (实例ID -> 实例) */
    this.services = new Map()
  }

  /**
   * 注册服务实例。同一 instanceId 重复注册会覆盖（幂等）。
   * @param {string} serviceName 服务名称
   * @param {{ instanceId: string, address: string }} instance 服务实例
   */
  register(serviceName, instance) {
    if (!this.services.has(serviceName)) {
      this.services.set(serviceName, new Map())
    }
    this.services.get(serviceName).set(instance.instanceId, instance)
  }

  /**
   * 摘除服务实例。
   * @param {string} serviceName 服务名称
   * @param {string} instanceId  实例ID
   * @returns {boolean} true=摘除成功
   */
  deregister(serviceName, instanceId) {
    const instances = this.services.get(serviceName)
    if (!instances || !instances.has(instanceId)) {
      return false
    }
    instances.delete(instanceId)
    return true
  }

  /**
   * 获取指定服务的所有可用实例（按 instanceId 排序）。
   * @param {string} serviceName 服务名称
   * @returns {Array} 实例列表
   */
  instances(serviceName) {
    const instances = Array.from((this.services.get(serviceName) || new Map()).values())
    // 排序保证轮询结果的确定性
    return instances.sort((a, b) => a.instanceId.localeCompare(b.instanceId))
  }
}

/**
 * RoundRobinDiscoverer - 轮询服务发现客户端
 * 【设计模式】策略模式：封装轮询选择策略。
 */
export class RoundRobinDiscoverer {
  /**
   * @param {ServiceRegistry} registry 关联的注册中心
   */
  constructor(registry) {
    this.registry = registry
    /** @type {Map<string, number>} 每个服务的轮询偏移量 */
    this.offsets = new Map()
  }

  /**
   * 获取下一个可用实例（轮询策略）。
   * @param {string} serviceName 服务名称
   * @returns {object|null} 下一个实例
   */
  next(serviceName) {
    const instances = this.registry.instances(serviceName)
    if (instances.length === 0) {
      return null
    }
    // 取模实现轮询
    const index = (this.offsets.get(serviceName) || 0) % instances.length
    this.offsets.set(serviceName, (this.offsets.get(serviceName) || 0) + 1)
    return instances[index]
  }
}

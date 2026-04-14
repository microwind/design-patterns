/**
 * @file balancer.js - 负载均衡模式（Load Balancing Pattern）的 JavaScript 实现
 *
 * 本模块演示三种经典负载均衡算法：轮询、加权轮询和最少连接。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：三种负载均衡算法是可互换的策略，
 *     各自实现不同的分配逻辑，调用方根据场景选择。
 *   - 迭代器模式（Iterator Pattern）：RoundRobin 和 WeightedRoundRobin
 *     的 next() 方法通过取模实现循环迭代。
 *
 * 【架构思想】
 *   负载均衡将流量分散到多个后端实例，避免单点过载。
 *
 * 【开源对比】
 *   - PM2（Node.js 进程管理器）：内置 cluster 模式的 round-robin 负载均衡
 *   - Nginx：作为 Node.js 前置反向代理，支持多种负载均衡策略
 *   本示例省略了健康检查和动态权重调整等工程细节，聚焦于算法核心。
 */

export class RoundRobinBalancer {
  constructor(backends) {
    this.backends = backends
    this.nextIndex = 0
  }

  next() {
    const backend = this.backends[this.nextIndex % this.backends.length]
    this.nextIndex++
    return backend
  }
}

export class WeightedRoundRobinBalancer {
  constructor(backends) {
    this.sequence = []
    this.nextIndex = 0

    for (const backend of backends) {
      const weight = backend.weight > 0 ? backend.weight : 1
      for (let i = 0; i < weight; i++) {
        this.sequence.push(backend)
      }
    }
  }

  next() {
    const backend = this.sequence[this.nextIndex % this.sequence.length]
    this.nextIndex++
    return backend
  }
}

export class LeastConnectionsBalancer {
  constructor(backends) {
    this.backends = new Map()
    for (const backend of backends) {
      this.backends.set(backend.backendId, {
        backendId: backend.backendId,
        weight: backend.weight || 1,
        activeConnections: backend.activeConnections || 0
      })
    }
  }

  acquire() {
    let chosen = null
    for (const backend of this.backends.values()) {
      if (!chosen || backend.activeConnections < chosen.activeConnections) {
        chosen = backend
      }
    }
    chosen.activeConnections++
    return chosen
  }

  release(backendId) {
    const backend = this.backends.get(backendId)
    if (backend && backend.activeConnections > 0) {
      backend.activeConnections--
    }
  }
}

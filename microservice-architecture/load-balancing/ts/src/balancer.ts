/**
 * @file balancer.ts - 负载均衡模式（Load Balancing Pattern）的 TypeScript 实现
 *
 * 本模块演示三种经典负载均衡算法：轮询、加权轮询和最少连接。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：三种负载均衡算法是可互换的策略，
 *     各自实现不同的分配逻辑，调用方根据场景选择。
 *     TypeScript 版本利用类型系统提供了更好的类型安全。
 *   - 迭代器模式（Iterator Pattern）：RoundRobin 和 WeightedRoundRobin
 *     的 next() 方法通过取模实现循环迭代。
 *
 * 【架构思想】
 *   负载均衡将流量分散到多个后端实例，避免单点过载。
 *
 * 【开源对比】
 *   - PM2 / Nginx：Node.js/TS 生态常用的负载均衡方案
 *   本示例省略了健康检查和动态权重调整等工程细节，聚焦于算法核心。
 */

export type Backend = {
  backendId: string;
  weight?: number;
  activeConnections?: number;
};

export class RoundRobinBalancer {
  private nextIndex = 0;

  constructor(private readonly backends: Backend[]) {}

  next(): Backend {
    const backend = this.backends[this.nextIndex % this.backends.length];
    this.nextIndex++;
    return backend;
  }
}

export class WeightedRoundRobinBalancer {
  private sequence: Backend[] = [];
  private nextIndex = 0;

  constructor(backends: Backend[]) {
    for (const backend of backends) {
      const weight = backend.weight && backend.weight > 0 ? backend.weight : 1;
      for (let i = 0; i < weight; i++) {
        this.sequence.push(backend);
      }
    }
  }

  next(): Backend {
    const backend = this.sequence[this.nextIndex % this.sequence.length];
    this.nextIndex++;
    return backend;
  }
}

export class LeastConnectionsBalancer {
  private backends: Record<string, Required<Backend>> = {};

  constructor(backends: Backend[]) {
    for (const backend of backends) {
      this.backends[backend.backendId] = {
        backendId: backend.backendId,
        weight: backend.weight ?? 1,
        activeConnections: backend.activeConnections ?? 0
      };
    }
  }

  acquire(): Required<Backend> {
    const backend = Object.values(this.backends).reduce((best, current) =>
      current.activeConnections < best.activeConnections ? current : best
    );
    backend.activeConnections++;
    return backend;
  }

  release(backendId: string): void {
    const backend = this.backends[backendId];
    if (backend && backend.activeConnections > 0) {
      backend.activeConnections--;
    }
  }
}

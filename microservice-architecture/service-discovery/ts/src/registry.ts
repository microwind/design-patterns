/**
 * @file registry.ts - 服务发现模式（Service Discovery Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 注册表模式（Registry Pattern）：ServiceRegistry 维护服务名到实例列表的映射。
 *   - 策略模式（Strategy Pattern）：RoundRobinDiscoverer 封装轮询选择策略。
 *
 * 【架构思想】
 *   TypeScript 的类型系统通过 ServiceInstance 类型和 Record 泛型，
 *   在编译期保证了注册表数据结构的类型安全。
 *
 * 【开源对比】
 *   - consul（npm）：支持 TypeScript 类型定义
 *   本示例用内存 Record 简化。
 */

/** 服务实例类型 */
export type ServiceInstance = {
  instanceId: string;  // 实例唯一标识
  address: string;     // 实例网络地址
};

/**
 * ServiceRegistry - 服务注册中心
 * 【设计模式】注册表模式：维护服务名到实例列表的全局映射。
 */
export class ServiceRegistry {
  /** 服务注册表：服务名 -> {实例ID -> 实例} */
  private services: Record<string, Record<string, ServiceInstance>> = {};

  /** 注册服务实例。同一 instanceId 重复注册会覆盖（幂等）。 */
  register(serviceName: string, instance: ServiceInstance): void {
    if (!this.services[serviceName]) {
      this.services[serviceName] = {};
    }
    this.services[serviceName][instance.instanceId] = instance;
  }

  /** 摘除服务实例。返回是否摘除成功。 */
  deregister(serviceName: string, instanceId: string): boolean {
    const instances = this.services[serviceName];
    if (!instances || !instances[instanceId]) {
      return false;
    }
    delete instances[instanceId];
    return true;
  }

  /** 获取指定服务的所有可用实例（按 instanceId 排序）。 */
  instances(serviceName: string): ServiceInstance[] {
    return Object.values(this.services[serviceName] ?? {}).sort((a, b) =>
      a.instanceId.localeCompare(b.instanceId)
    );
  }
}

/**
 * RoundRobinDiscoverer - 轮询服务发现客户端
 * 【设计模式】策略模式：封装轮询选择策略。
 */
export class RoundRobinDiscoverer {
  /** 每个服务的轮询偏移量 */
  private offsets: Record<string, number> = {};

  constructor(private readonly registry: ServiceRegistry) {}

  /** 获取下一个可用实例（轮询策略）。无可用实例时返回 null。 */
  next(serviceName: string): ServiceInstance | null {
    const instances = this.registry.instances(serviceName);
    if (instances.length === 0) {
      return null;
    }
    // 取模实现轮询
    const index = (this.offsets[serviceName] ?? 0) % instances.length;
    this.offsets[serviceName] = (this.offsets[serviceName] ?? 0) + 1;
    return instances[index];
  }
}

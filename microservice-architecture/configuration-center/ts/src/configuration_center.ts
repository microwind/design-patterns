/**
 * @file configuration_center.ts - 配置中心模式（Configuration Center Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 观察者模式：实际工程中配置变更会推送通知，本示例简化为客户端主动 refresh。
 *   - 代理模式：ConfigClient 代理 ConfigCenter 访问并缓存配置。
 *
 * 【架构思想】
 *   TypeScript 通过 ServiceConfig 类型定义配置结构，在编译期保证类型安全。
 *
 * 【开源对比】
 *   - 实际工程中接入 Apollo / Nacos 等远程配置中心
 *   本示例用内存 Record 简化。
 */

/** 服务配置类型 */
export type ServiceConfig = {
  serviceName: string;        // 服务名称
  environment: string;        // 环境标识
  version: number;            // 配置版本号
  dbHost: string;             // 数据库地址
  timeoutMs: number;          // 超时时间（毫秒）
  featureOrderAudit: boolean; // 订单审计功能开关
};

/**
 * ConfigCenter - 配置中心服务端
 * 【设计模式】注册表模式：按 "serviceName@environment" 键存储配置。
 */
export class ConfigCenter {
  /** 配置存储 */
  private store: Record<string, ServiceConfig> = {};

  /** 发布配置。同一 key 重复发布会覆盖。 */
  put(config: ServiceConfig): void {
    this.store[`${config.serviceName}@${config.environment}`] = config;
  }

  /** 获取指定服务和环境的配置。 */
  get(serviceName: string, environment: string): ServiceConfig | null {
    return this.store[`${serviceName}@${environment}`] ?? null;
  }
}

/**
 * ConfigClient - 配置客户端
 * 【设计模式】代理模式：代理 ConfigCenter 访问，本地缓存配置快照。
 */
export class ConfigClient {
  /** 当前缓存的配置快照 */
  private currentConfig: ServiceConfig | null = null;

  constructor(
    private readonly center: ConfigCenter,
    private readonly serviceName: string,
    private readonly environment: string
  ) {}

  /** 首次加载配置 */
  load(): ServiceConfig | null {
    this.currentConfig = this.center.get(this.serviceName, this.environment);
    return this.currentConfig;
  }

  /** 刷新配置（重新拉取） */
  refresh(): ServiceConfig | null {
    return this.load();
  }

  /** 获取当前缓存的配置快照 */
  current(): ServiceConfig | null {
    return this.currentConfig;
  }
}

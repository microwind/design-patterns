/**
 * @file router.ts - API 版本管理模式（API Versioning Pattern）的 TypeScript 实现
 *
 * 支持 URL 路径版本（/v1/、/v2/）和 Header 版本（X-API-Version），提供默认版本兜底。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：不同版本的处理器是可互换的策略。
 *     TypeScript 版本利用类型系统提供了更好的类型安全。
 *   - 工厂方法模式（Factory Method）：resolveVersion 根据请求上下文决定版本。
 *   - 模板方法模式（Template Method）：handle 定义了固定的处理骨架。
 *
 * 【架构思想】
 *   API 版本管理让新老客户端并行使用不同版本接口，实现平滑演进。
 *
 * 【开源对比】
 *   - NestJS：@Version() 装饰器 + URI/Header/Media Type 多种版本策略
 *   本示例省略了版本协商和废弃通知等工程细节，聚焦于版本解析和路由分发。
 */

export type Request = {
  path: string;
  headers: Record<string, string>;
};

export type Response = {
  statusCode: number;
  version: string;
  body: string;
};

export class VersionedRouter {
  private handlers: Record<string, () => string> = {};

  constructor(private readonly defaultVersion: string) {}

  register(version: string, handler: () => string): void {
    this.handlers[this.normalize(version)] = handler;
  }

  handle(request: Request): Response {
    const version = this.resolveVersion(request);
    const handler = this.handlers[version];
    if (!handler) {
      return { statusCode: 400, version, body: "unsupported api version" };
    }
    return { statusCode: 200, version, body: handler() };
  }

  resolveVersion(request: Request): string {
    const path = request.path.toLowerCase();
    if (path.includes("/v2/")) {
      return "v2";
    }
    if (path.includes("/v1/")) {
      return "v1";
    }
    const headerVersion = this.normalize(request.headers["X-API-Version"] ?? "");
    if (headerVersion) {
      return headerVersion;
    }
    return this.normalize(this.defaultVersion);
  }

  private normalize(version: string): string {
    const normalized = version.trim().toLowerCase();
    if (!normalized) {
      return "";
    }
    return normalized.startsWith("v") ? normalized : `v${normalized}`;
  }
}

export function productHandlerV1(): string {
  return '{"id":"P100","name":"Mechanical Keyboard"}';
}

export function productHandlerV2(): string {
  return '{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}';
}

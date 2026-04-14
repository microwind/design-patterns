/**
 * @file gateway.ts - API 网关模式（API Gateway Pattern）的 TypeScript 实现
 *
 * API 网关是微服务架构的统一入口，负责请求路由、中间件处理和跨切面关注点。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：网关为客户端提供统一入口，屏蔽后端服务拆分细节。
 *   - 责任链模式（Chain of Responsibility）：中间件按注册顺序依次执行，
 *     任一中间件可拦截请求并直接返回响应。
 *   - 策略模式（Strategy Pattern）：Handler 和 Middleware 作为类型化函数，
 *     不同的处理器和中间件是可插拔的策略。
 *     TypeScript 版本利用类型系统提供了更好的类型安全。
 *
 * 【架构思想】
 *   API 网关集中处理认证、限流、日志、链路追踪等跨切面关注点。
 *
 * 【开源对比】
 *   - NestJS：原生 TypeScript 的 Node.js 框架，支持中间件、守卫、拦截器
 *   - tRPC：端到端类型安全的 API 层
 *   本示例省略了异步 I/O、动态路由等工程细节，聚焦于路由匹配和中间件链。
 */

export type Request = {
  method: string;
  path: string;
  headers: Record<string, string>;
};

export type Response = {
  statusCode: number;
  body: string;
  headers: Record<string, string>;
};

export type Handler = (request: Request) => Response;
export type Middleware = (request: Request) => Response | null;

export class APIGateway {
  private routes: Record<string, Handler> = {};
  private middlewares: Middleware[] = [];

  use(middleware: Middleware): void {
    this.middlewares.push(middleware);
  }

  register(prefix: string, handler: Handler): void {
    this.routes[prefix] = handler;
  }

  handle(request: Request): Response {
    for (const middleware of this.middlewares) {
      const response = middleware(request);
      if (response !== null) {
        return response;
      }
    }

    const handler = this.match(request.path);
    if (!handler) {
      return { statusCode: 404, body: "gateway: route not found", headers: {} };
    }

    const response = handler(request);
    if (!response.headers["X-Correlation-ID"]) {
      response.headers["X-Correlation-ID"] =
        request.headers["X-Correlation-ID"] ?? "gw-generated-correlation-id";
    }
    return response;
  }

  private match(path: string): Handler | null {
    let matchedPrefix = "";
    let matchedHandler: Handler | null = null;

    for (const [prefix, handler] of Object.entries(this.routes)) {
      if (path.startsWith(prefix) && prefix.length > matchedPrefix.length) {
        matchedPrefix = prefix;
        matchedHandler = handler;
      }
    }

    return matchedHandler;
  }
}

export function requireUserHeader(prefix: string, headerName: string): Middleware {
  return (request: Request): Response | null => {
    if (!request.path.startsWith(prefix)) {
      return null;
    }
    if (!request.headers[headerName]) {
      return { statusCode: 401, body: "gateway: unauthorized", headers: {} };
    }
    return null;
  };
}

export function orderServiceHandler(): Handler {
  return (request: Request): Response => ({
    statusCode: 200,
    body: `order-service handled ${request.path}`,
    headers: { "X-Upstream-Service": "order-service" }
  });
}

export function inventoryServiceHandler(): Handler {
  return (request: Request): Response => ({
    statusCode: 200,
    body: `inventory-service handled ${request.path}`,
    headers: { "X-Upstream-Service": "inventory-service" }
  });
}

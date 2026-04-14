/**
 * @file gateway.js - API 网关模式（API Gateway Pattern）的 JavaScript 实现
 *
 * API 网关是微服务架构的统一入口，负责请求路由、中间件处理和跨切面关注点。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：网关为客户端提供统一入口，屏蔽后端服务拆分细节。
 *   - 责任链模式（Chain of Responsibility）：中间件按注册顺序依次执行，
 *     任一中间件可拦截请求并直接返回响应。
 *   - 策略模式（Strategy Pattern）：Handler 和 Middleware 作为函数，
 *     不同的处理器和中间件是可插拔的策略。
 *
 * 【架构思想】
 *   API 网关集中处理认证、限流、日志、链路追踪等跨切面关注点。
 *
 * 【开源对比】
 *   - Express.js / Koa.js：Node.js Web 框架，中间件模式是其核心设计
 *   - express-gateway：基于 Express 的 API 网关
 *   本示例省略了异步 I/O、动态路由等工程细节，聚焦于路由匹配和中间件链。
 */

export class APIGateway {
  constructor() {
    this.routes = new Map()
    this.middlewares = []
  }

  use(middleware) {
    this.middlewares.push(middleware)
  }

  register(prefix, handler) {
    this.routes.set(prefix, handler)
  }

  handle(request) {
    for (const middleware of this.middlewares) {
      const response = middleware(request)
      if (response !== null) {
        return response
      }
    }

    const handler = this.match(request.path)
    if (!handler) {
      return { statusCode: 404, body: 'gateway: route not found', headers: {} }
    }

    const response = handler(request)
    response.headers['X-Correlation-ID'] =
      response.headers['X-Correlation-ID'] ||
      request.headers['X-Correlation-ID'] ||
      'gw-generated-correlation-id'
    return response
  }

  match(path) {
    let matchedPrefix = ''
    let matchedHandler = null

    for (const [prefix, handler] of this.routes.entries()) {
      if (path.startsWith(prefix) && prefix.length > matchedPrefix.length) {
        matchedPrefix = prefix
        matchedHandler = handler
      }
    }

    return matchedHandler
  }
}

export function requireUserHeader(prefix, headerName) {
  return (request) => {
    if (!request.path.startsWith(prefix)) {
      return null
    }
    if (!request.headers[headerName]) {
      return { statusCode: 401, body: 'gateway: unauthorized', headers: {} }
    }
    return null
  }
}

export function orderServiceHandler() {
  return (request) => ({
    statusCode: 200,
    body: `order-service handled ${request.path}`,
    headers: { 'X-Upstream-Service': 'order-service' }
  })
}

export function inventoryServiceHandler() {
  return (request) => ({
    statusCode: 200,
    body: `inventory-service handled ${request.path}`,
    headers: { 'X-Upstream-Service': 'inventory-service' }
  })
}

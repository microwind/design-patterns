/**
 * @file router.js - API 版本管理模式（API Versioning Pattern）的 JavaScript 实现
 *
 * 支持 URL 路径版本（/v1/、/v2/）和 Header 版本（X-API-Version），提供默认版本兜底。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：不同版本的处理器是可互换的策略，
 *     路由器根据解析到的版本号选择对应策略执行。
 *   - 工厂方法模式（Factory Method）：resolveVersion 根据请求上下文决定使用哪个版本。
 *   - 模板方法模式（Template Method）：handle 定义了"解析版本 → 查找处理器 → 执行"的骨架。
 *
 * 【架构思想】
 *   API 版本管理让新老客户端并行使用不同版本接口，实现平滑演进。
 *
 * 【开源对比】
 *   - Express.js：通过 express.Router() 挂载不同版本路由前缀
 *   - Hapi.js：支持路由级别的版本配置
 *   本示例省略了版本协商和废弃通知等工程细节，聚焦于版本解析和路由分发。
 */

export class VersionedRouter {
  constructor(defaultVersion) {
    this.defaultVersion = this.normalize(defaultVersion)
    this.handlers = new Map()
  }

  register(version, handler) {
    this.handlers.set(this.normalize(version), handler)
  }

  handle(request) {
    const version = this.resolveVersion(request)
    const handler = this.handlers.get(version)
    if (!handler) {
      return { statusCode: 400, version, body: 'unsupported api version' }
    }
    return { statusCode: 200, version, body: handler() }
  }

  resolveVersion(request) {
    const path = request.path.toLowerCase()
    if (path.includes('/v2/')) {
      return 'v2'
    }
    if (path.includes('/v1/')) {
      return 'v1'
    }
    const headerVersion = this.normalize(request.headers['X-API-Version'] || '')
    if (headerVersion) {
      return headerVersion
    }
    return this.defaultVersion
  }

  normalize(version) {
    const normalized = version.trim().toLowerCase()
    if (!normalized) {
      return ''
    }
    return normalized.startsWith('v') ? normalized : `v${normalized}`
  }
}

export function productHandlerV1() {
  return '{"id":"P100","name":"Mechanical Keyboard"}'
}

export function productHandlerV2() {
  return '{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}'
}

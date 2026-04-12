/**
 * @file idempotency.js - 幂等模式（Idempotency Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 备忘录模式（Memento Pattern）：首次执行结果被存储，后续重复请求返回备忘结果。
 *   - 代理模式（Proxy Pattern）：幂等层包裹在业务逻辑之外，透明拦截重复请求。
 *
 * 【架构思想】
 *   幂等模式通过 idempotencyKey 将重复请求折叠为同一结果。
 *
 * 【开源对比】
 *   - Express/Koa 中间件：在请求层统一处理幂等（Redis 存储）
 *   本示例用内存 Map 简化。
 */

/**
 * IdempotencyOrderService - 带幂等保护的订单服务
 *
 * 三条路径：首次 → CREATED，重复+匹配 → replayed，重复+不匹配 → CONFLICT。
 */
export class IdempotencyOrderService {
  constructor() {
    /** @type {Map<string, {fingerprint: string, response: object}>} 幂等存储 */
    this.store = new Map()
  }

  /**
   * 创建订单（带幂等保护）。
   * @param {string} idempotencyKey 幂等键
   * @param {string} orderId        订单ID
   * @param {string} sku            商品SKU
   * @param {number} quantity       数量
   * @returns {{ orderId, sku, quantity, status, replayed }} 订单响应
   */
  createOrder(idempotencyKey, orderId, sku, quantity) {
    // 计算请求指纹
    const fingerprint = `${orderId}|${sku}|${quantity}`
    const existing = this.store.get(idempotencyKey)
    if (existing) {
      // 同一幂等键但参数不同 → 冲突
      if (existing.fingerprint !== fingerprint) {
        return { orderId, sku, quantity, status: 'CONFLICT', replayed: false }
      }
      // 同一幂等键且参数相同 → 返回存储的结果
      return { ...existing.response, replayed: true }
    }

    // 首次请求 → 执行业务逻辑并存储结果
    const response = { orderId, sku, quantity, status: 'CREATED', replayed: false }
    this.store.set(idempotencyKey, { fingerprint, response })
    return response
  }
}

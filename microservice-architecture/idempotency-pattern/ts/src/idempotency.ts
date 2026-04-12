/**
 * @file idempotency.ts - 幂等模式（Idempotency Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 备忘录模式（Memento Pattern）：首次执行结果被存储，后续重复请求返回备忘结果。
 *   - 代理模式（Proxy Pattern）：幂等层包裹在业务逻辑之外，透明拦截重复请求。
 *
 * 【架构思想】
 *   TypeScript 通过 OrderResponse 类型和 StoredResult 类型提供编译期安全。
 *
 * 【开源对比】
 *   - NestJS 拦截器 + Redis：在请求层统一处理幂等
 *   本示例用内存 Record 简化。
 */

/** 订单响应类型。replayed 区分首次/重复请求。 */
export type OrderResponse = {
  orderId: string;
  sku: string;
  quantity: number;
  status: string;     // CREATED / CONFLICT
  replayed: boolean;  // 是否为重放结果
};

/** 存储的幂等结果（内部类型） */
type StoredResult = {
  fingerprint: string;    // 请求指纹
  response: OrderResponse; // 首次执行的响应
};

/**
 * IdempotencyOrderService - 带幂等保护的订单服务
 */
export class IdempotencyOrderService {
  /** 幂等存储：key → StoredResult */
  private store: Record<string, StoredResult> = {};

  /**
   * 创建订单（带幂等保护）。
   * 三条路径：首次→CREATED，重复+匹配→replayed，重复+不匹配→CONFLICT。
   */
  createOrder(idempotencyKey: string, orderId: string, sku: string, quantity: number): OrderResponse {
    // 计算请求指纹
    const fingerprint = `${orderId}|${sku}|${quantity}`;
    const existing = this.store[idempotencyKey];
    if (existing) {
      // 同一幂等键但参数不同 → 冲突
      if (existing.fingerprint !== fingerprint) {
        return { orderId, sku, quantity, status: "CONFLICT", replayed: false };
      }
      // 同一幂等键且参数相同 → 返回存储的结果
      return { ...existing.response, replayed: true };
    }

    // 首次请求 → 执行业务逻辑并存储结果
    const response: OrderResponse = { orderId, sku, quantity, status: "CREATED", replayed: false };
    this.store[idempotencyKey] = { fingerprint, response };
    return response;
  }
}

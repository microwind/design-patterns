/**
 * @file InventoryClient.ts - 库存服务契约接口
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：OrderService 依赖此接口而非具体实现。
 *   - 策略模式（Strategy Pattern）：不同的实现类代表不同的调用策略。
 *
 * 【架构思想】
 *   TypeScript 的 interface 在编译后会被擦除，但在开发时提供了类型安全保障。
 *   服务间通过契约接口解耦，调用方不关心被调服务是本地还是远程。
 *
 * 【开源对比】
 *   - gRPC：通过 .proto 文件自动生成 TypeScript 接口
 *   - tRPC：端到端类型安全的 RPC 框架
 */

/** 库存服务契约接口 */
export interface InventoryClient {
  /**
   * 预留库存
   * @param sku      商品 SKU 编码
   * @param quantity 预留数量
   * @returns true=预留成功，false=库存不足
   */
  reserve(sku: string, quantity: number): boolean
}

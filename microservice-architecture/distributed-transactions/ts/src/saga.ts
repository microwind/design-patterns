/**
 * @file saga.ts - 分布式事务 Saga 模式的 TypeScript 实现
 *
 * 【设计模式】
 *   - 命令模式：每个步骤和补偿是独立命令。
 *   - 责任链模式：正向步骤按链式顺序执行。
 *   - 状态模式：订单状态 PENDING → COMPLETED / CANCELLED。
 *
 * 【架构思想】
 *   TypeScript 通过 SagaOrder 类型和 private/readonly 修饰符提供类型安全。
 *
 * 【开源对比】
 *   - Temporal TypeScript SDK：原生 TS 工作流引擎
 *   本示例用同步方法调用模拟编排式 Saga。
 */

/** Saga 订单类型 */
export type SagaOrder = {
  orderId: string;
  status: string;  // PENDING / COMPLETED / CANCELLED
};

/** 库存服务：提供正向操作和补偿操作 */
class InventoryService {
  constructor(public bookStock: number) {}

  /** 正向步骤：预占库存 */
  reserve(sku: string, quantity: number): boolean {
    if (sku !== "SKU-BOOK" || quantity <= 0 || this.bookStock < quantity) {
      return false;
    }
    this.bookStock -= quantity;
    return true;
  }

  /** 补偿动作：释放已预占的库存 */
  release(sku: string, quantity: number): void {
    if (sku === "SKU-BOOK" && quantity > 0) {
      this.bookStock += quantity;
    }
  }
}

/** 支付服务 */
class PaymentService {
  constructor(private readonly fail: boolean) {}

  /** 正向步骤：扣款 */
  charge(orderId: string): boolean {
    return !this.fail;
  }
}

/**
 * SagaCoordinator - Saga 协调者（编排式）
 * 【设计模式】命令模式 + 责任链：按序执行步骤，失败时逆序补偿。
 */
export class SagaCoordinator {
  readonly inventory: InventoryService;
  private readonly payment: PaymentService;

  constructor(stock: number, paymentFails: boolean) {
    this.inventory = new InventoryService(stock);
    this.payment = new PaymentService(paymentFails);
  }

  /** 执行 Saga 事务：库存预占 → 支付 → 成功/补偿。 */
  execute(orderId: string, sku: string, quantity: number): SagaOrder {
    const order: SagaOrder = { orderId, status: "PENDING" };

    // 步骤1：库存预占
    if (!this.inventory.reserve(sku, quantity)) {
      order.status = "CANCELLED";
      return order;
    }

    // 步骤2：支付扣款
    if (!this.payment.charge(orderId)) {
      // 支付失败 → 补偿：释放库存
      this.inventory.release(sku, quantity);
      order.status = "CANCELLED";
      return order;
    }

    // 全部成功
    order.status = "COMPLETED";
    return order;
  }
}

/**
 * @file Order.ts - 订单实体（值对象）
 *
 * 【设计模式】值对象模式：创建后状态不再改变，所有属性为 readonly。
 *
 * 【架构思想】
 *   TypeScript 的联合类型（'CREATED' | 'REJECTED'）在编译期保证了
 *   status 字段的取值安全，比运行时字符串校验更可靠。
 */

/** 订单实体 */
export class Order {
  constructor(
    /** 订单ID */
    public readonly orderId: string,
    /** 商品SKU编码 */
    public readonly sku: string,
    /** 订购数量 */
    public readonly quantity: number,
    /** 订单状态：CREATED（成功）/ REJECTED（库存不足） */
    public readonly status: 'CREATED' | 'REJECTED'
  ) {}
}

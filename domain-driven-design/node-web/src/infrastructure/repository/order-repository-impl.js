// 基础设施层（Infrastructure）：订单仓储实现
// src/infrastructure/repository/order-repository-impl.js
import OrderRepository from '../../domain/order/order-repository.js';
export class OrderRepositoryImpl extends OrderRepository {
  constructor() {
    super(); 
    this.orders = new Map();
  }

  // 保存订单
  async save(order) {
    this.orders.set(order.id, order);
  }

  // 根据ID查找订单
  async findByID(id) {
    const order = this.orders.get(id);
    if (!order) {
      throw new Error(`订单 ID ${id} 不存在`);
    }
    return order;
  }

  // 查找所有订单
  async findAll(userId) {
    // 根据userId查询数据库
    return Array.from(this.orders.values());
  }

  // 删除订单
  async delete(id) {
    if (!this.orders.has(id)) {
      throw new Error(`订单 ID ${id} 不存在，无法删除`);
    }
    this.orders.delete(id);
  }

  // 根据客户名称查找订单
  async findByCustomerName(customerName) {
    const result = Array.from(this.orders.values()).filter(order => order.customerName === customerName);
    if (result.length === 0) {
      throw new Error(`没有找到客户名称为 ${customerName} 的订单`);
    }
    return result;
  }
}
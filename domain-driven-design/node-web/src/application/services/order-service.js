// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
// src/application/services/order-service.js

import Order from '../../domain/order/order.js';

export default class OrderService {
  constructor(orderRepository) {
    this.orderRepository = orderRepository;
  }

  // 创建订单并保存到仓储中
  async createOrder(customerName, amount) {
    // 自动生成订单 ID
    const id = await this.generateOrderId();

    // 创建订单
    const newOrder = Order.newOrder(id, customerName, amount);
    if (!newOrder) {
      throw new Error('订单创建失败');
    }

    try {
      await this.orderRepository.save(newOrder);
      return newOrder;
    } catch (error) {
      throw new Error(`订单保存失败: ${error.message}`);
    }
  }

  // 自动生成订单 ID ，实际应用中会采用分布式ID或者采用数据库自增键
  async generateOrderId() {
    // 这里可以根据业务需求生成唯一的 ID。如：使用时间戳 + 随机数
    const timestamp = Date.now();
    const random = Math.floor(Math.random() * 1000);
    return parseInt(`${timestamp}${random}`);
  }

  // 取消订单
  async cancelOrder(id) {
    try {
      const order = await this.orderRepository.findByID(id);
      order.cancel();
      await this.orderRepository.save(order);
    } catch (error) {
      throw new Error(`订单取消失败: ${error.message}`);
    }
  }

  // 查询订单
  async getOrder(id) {
    try {
      return await this.orderRepository.findByID(id);
    } catch (error) {
      throw new Error(`查询订单失败: ${error.message}`);
    }
  }

  // 查询全部订单，省略分页
  async getAllOrders(userId) {
    try {
      return await this.orderRepository.findAll(userId);
    } catch (error) {
      throw new Error(`查询全部订单失败: ${error.message}`);
    }
  }

  // 更新订单的客户信息和金额
  async updateOrder(id, customerName, amount) {
    try {
      const order = await this.orderRepository.findByID(id);
      order.updateCustomerInfo(customerName);
      order.updateAmount(amount);
      await this.orderRepository.save(order);
      return order;
    } catch (error) {
      throw new Error(`更新订单失败: ${error.message}`);
    }
  }

  // 删除订单
  async deleteOrder(id) {
    try {
      const order = await this.orderRepository.findByID(id);
      await this.orderRepository.delete(order.id);
    } catch (error) {
      throw new Error(`删除订单失败: ${error.message}`);
    }
  }
}
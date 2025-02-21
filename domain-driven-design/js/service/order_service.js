// 服务层，提供高层业务逻辑
import {
  Order
} from '../domain/order.js';
import {
  OrderRepository
} from '../repository/order_repository.js';

export class OrderService {
  constructor() {
    this.orderRepository = new OrderRepository();
  }

  createOrder(id, customerName, amount) {
    const order = new Order(id, customerName, amount);
    this.orderRepository.save(order);
    console.log(`订单 ID ${id} 创建成功`);
  }

  cancelOrder(id) {
    console.log(`取消订单 ID ${id}`);
    const order = this.orderRepository.findById(id);
    if (order) {
      order.cancel();
    } else {
      console.log(`未找到 ID ${id}`);
    }
  }

  queryOrder(id) {
    console.log(`查询订单 ID ${id}`);
    return this.orderRepository.findById(id);
  }

  viewOrderHistory() {
    console.log(`查询全部订单历史`);
    return this.orderRepository.findAll();
  }
}
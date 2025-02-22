// 领域层(Domain)：领域实体
// src/domain/order/order.js

const OrderStatus = {
  CREATED: 0,
  CANCELED: 1,
};

export default class Order {
  constructor(id, customerName, amount, status = OrderStatus.CREATED) {
    this.id = id;
    this.customerName = customerName;
    this.amount = amount;
    this.status = status;
  }

  // 创建新订单
  static newOrder(id, customerName, amount) {
    if (amount <= 0) {
      console.log('订单金额无效');
      return null;
    }
    return new Order(id, customerName, amount);
  }

  // 取消订单
  cancel() {
    if (this.status === OrderStatus.CREATED) {
      this.status = OrderStatus.CANCELED;
      console.log(`订单 ID ${this.id} 已取消`);
    } else {
      console.log(`订单 ID ${this.id} 已取消，无法重复取消`);
    }
  }

  // 更新客户名称
  updateCustomerInfo(newCustomerName) {
    this.customerName = newCustomerName;
    console.log(`订单 ID ${this.id} 的客户名称已更新为: ${this.customerName}`);
  }

  // 更新订单金额
  updateAmount(newAmount) {
    if (newAmount <= 0) {
      console.log('更新金额无效');
      return;
    }
    this.amount = newAmount;
    console.log(`订单 ID ${this.id} 的金额已更新为: ${this.amount}`);
  }

  // 显示订单信息
  display() {
    console.log(`订单 ID: ${this.id}\n客户名称: ${this.customerName}\n订单金额: ${this.amount}\n订单状态: ${this.statusToString()}`);
  }

  // 获取订单状态的字符串表示
  statusToString() {
    switch (this.status) {
      case OrderStatus.CREATED:
        return '已创建';
      case OrderStatus.CANCELED:
        return '已取消';
      default:
        return '未知状态';
    }
  }
}
// 领域层，包含核心的业务模型和逻辑
// domain/order.js
export class Order {
  constructor(id, customerName, amount) {
    this.id = id;
    this.customerName = customerName;
    this.amount = amount;
    this.status = 'CREATED';
  }

  cancel() {
    if (this.status === 'CREATED') {
      this.status = 'CANCELED';
      console.log(`订单 ID ${this.id} 已取消`);
    } else {
      console.log(`订单 ID ${this.id} 已经取消，无法重复操作`);
    }
  }
}
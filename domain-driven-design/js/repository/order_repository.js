// 仓储层，负责与数据源进行交互
// repository/order_repository.js
export class OrderRepository {
    constructor() {
        this.orders = new Map();
    }

    save(order) {
        this.orders.set(order.id, order);
    }

    findById(id) {
        return this.orders.get(id) || null;
    }

    findAll() {
        return Array.from(this.orders.values());
    }

    clear() {
        this.orders.clear();
        console.log("所有订单已清理");
    }
}
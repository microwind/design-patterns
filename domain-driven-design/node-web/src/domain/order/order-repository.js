// src/domain/order/order-repository.js
// 模拟接口供基础层实现，JS因为没有接口，此处用class表示，如果未实现对应函数则发出警告
export default class OrderRepository {
  // 保存实体
  async save(entity) {
    throw new Error('save 方法未实现');
  }

  // 根据ID查找实体
  async findByID(id) {
    throw new Error('findByID 方法未实现');
  }

  // 查找所有实体
  async findAll() {
    throw new Error('findAll 方法未实现');
  }

  // 删除实体
  async delete(id) {
    throw new Error('delete 方法未实现');
  }

  // 根据客户名称查找订单
  async findByCustomerName(customerName) {
    throw new Error('findByCustomerName 方法未实现');
  }
}
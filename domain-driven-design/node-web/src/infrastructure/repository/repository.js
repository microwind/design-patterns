// src/infrastructure/repository/repository.js
// 模拟通用接口，JS因为没有接口字段，故此文件无用
class Repository {
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
}

module.exports = Repository;
package repository

// Repository 通用仓储接口，定义了常见的 CRUD 操作
type Repository[T any] interface {
  Save(entity *T) error         // 保存实体
  FindByID(id int) (*T, error)  // 根据ID查找实体
  FindAll() ([]*T, error)       // 查找所有实体
  Delete(id int) error          // 删除实体
}

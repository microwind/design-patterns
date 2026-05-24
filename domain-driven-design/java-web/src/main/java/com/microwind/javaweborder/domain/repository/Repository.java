// 领域层(Domain) - 通用仓库接口
//
// Repository 是 DDD 战术模式之一，它把"聚合根的集合"抽象成像
// 内存集合一样的对象，对外屏蔽底层存储细节。
//
// 关键约定：
// - 仓储以"聚合根"为单位（不存子实体）
// - 接口属于领域层；具体实现属于基础设施层（依赖倒置原则）
//
// 通过引入第二个类型参数 ID，可以让不同聚合使用各自专属的 ID 值对象
// （如 Order 用 OrderId、User 用 UserId），避免在签名上互相混淆。
package com.microwind.javaweborder.domain.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void delete(ID id);
}

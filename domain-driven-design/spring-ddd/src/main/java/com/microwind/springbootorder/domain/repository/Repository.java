// 领域层(Domain)：通用仓库接口
package com.microwind.springbootorder.domain.repository;

import java.util.List;
import java.util.Optional;

// 自定义仓库接口，本文件为通用接口，供业务继承[可选]
public interface Repository<T> {

    void save(T entity);              // 保存实体

    Optional<T> findById(long id);     // 根据 ID 查找实体

    List<T> findAll();                // 查找所有实体

    void delete(long id);              // 删除实体
}

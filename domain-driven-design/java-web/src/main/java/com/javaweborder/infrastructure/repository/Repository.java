package com.javaweborder.infrastructure.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    void save(T entity);              // 保存实体

    Optional<T> findById(long id);     // 根据 ID 查找实体

    List<T> findAll();                // 查找所有实体

    void delete(long id);              // 删除实体
}

package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

// User仓库接口文件，interface定义在Domain层，具体实现impl放在Infrastructure，体现依赖倒置
// JdbcTemplate：优点是灵活、可控性强、缺点是ORM 性能损耗，但代码冗余、需手动处理对象映射和事务。
public interface UserRepository {
    Optional<User> findById(Integer userId);  // 根据用户ID查询
    Optional<User> findByName(String userName);         // 根据用户名查询用户
    Page<User> findAllUsers(Pageable pageable);  // 分页查询所有用户
    User save(User user);  // 保存用户（插入或更新）
    void delete(User user);  // 删除用户
}
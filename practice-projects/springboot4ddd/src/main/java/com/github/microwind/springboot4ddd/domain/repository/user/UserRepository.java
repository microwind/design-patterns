package com.github.microwind.springboot4ddd.domain.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 * 定义用户领域对象的持久化操作
 *
 * @author jarry
 * @since 1.0.0
 */
public interface UserRepository {

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 保存后的用户对象（包含ID）
     */
    User save(User user);

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回Optional.empty()
     */
    Optional<User> findById(Long id);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 用户对象，不存在返回Optional.empty()
     */
    Optional<User> findByName(String name);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户对象，不存在返回Optional.empty()
     */
    Optional<User> findByEmail(String email);

    /**
     * 查找所有用户
     *
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 更新用户
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User update(User user);

    /**
     * 根据ID删除用户
     *
     * @param id 用户ID
     */
    void deleteById(Long id);

    /**
     * 检查用户名是否存在
     *
     * @param name 用户名
     * @return true存在，false不存在
     */
    boolean existsByName(String name);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true存在，false不存在
     */
    boolean existsByEmail(String email);
}

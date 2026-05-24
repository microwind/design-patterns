package com.github.microwind.springboot4ddd.domain.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 *
 * <p>零框架依赖：分页类型使用 domain 自定义的 {@link PageRequest} / {@link PageResult}。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    PageResult<User> findAll(PageRequest pageRequest);

    User update(User user);

    void deleteById(Long id);

    boolean existsByName(String name);

    boolean existsByEmail(String email);
}

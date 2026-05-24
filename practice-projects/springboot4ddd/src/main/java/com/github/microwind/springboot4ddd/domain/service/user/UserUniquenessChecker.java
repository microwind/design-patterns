package com.github.microwind.springboot4ddd.domain.service.user;

/**
 * 用户唯一性校验领域服务接口
 *
 * <p>用户名 / 邮箱在整个用户上下文中必须唯一，属于跨聚合实例的领域规则，
 * 因此抽象为领域服务由聚合根 / 工厂方法调用，实现位于 infrastructure。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface UserUniquenessChecker {

    boolean existsByName(String name);

    boolean existsByEmail(String email);
}

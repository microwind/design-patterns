package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import com.github.microwind.springboot4ddd.domain.service.user.UserUniquenessChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户唯一性校验领域服务实现。
 *
 * <p>位于 infrastructure 层，委托 {@link UserRepository} 完成实际查询。
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class UserUniquenessCheckerImpl implements UserUniquenessChecker {

    private final UserRepository userRepository;

    @Override
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

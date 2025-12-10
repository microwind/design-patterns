package com.microwind.knife.application.services;

import com.microwind.knife.application.dto.user.UserMapper;
import com.microwind.knife.domain.user.User;
import com.microwind.knife.domain.user.UserDomainService;
import com.microwind.knife.domain.repository.UserRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

 @Service
@RequiredArgsConstructor
public class UserService {
    // UserRepository接口有多种实现，可任选其一
    // 1. 采用Spring Data Jpa模式，代码更加简单，数据可持久化
    // private final UserJpaRepository userRepository;
    // 2. 采用Spring jdbcTemplate模式，纯SQL，性能更好
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserDomainService userDomainService;

    // 创建用户
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 查询用户
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    // 查询用户
    public Optional<User> getUserByName(String userName) {
        return userRepository.findByName(userName);
    }

    // 更新用户
    @Transactional
    public User updateUser(Integer userId, User user) {
        User existingOrder = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        if (existingOrder.getId() != null) {
            user.setId(userId);
            return userRepository.save(user);
        }
        return null;
    }
    
    // 删除用户
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
        userRepository.delete(user);
    }

    // 获取所有用户
    public Page<User> getAllUsers(Pageable pageable) {
        // 使用自定义查询
        return userRepository.findAllUsers(pageable);
    }

}

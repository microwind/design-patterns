package com.microwind.knife.application.services;

import com.microwind.knife.application.dto.user.UserMapper;
import com.microwind.knife.domain.user.User;
import com.microwind.knife.domain.user.UserDomainService;
import com.microwind.knife.domain.repository.UserRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import com.microwind.knife.interfaces.vo.user.CreateUserRequest;
import com.microwind.knife.interfaces.vo.user.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

 @Service
@RequiredArgsConstructor
public class UserService {
    // 采用Spring jdbcTemplate模式，纯SQL，性能更好
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserDomainService userDomainService;

    // 创建用户
    public User createUser(CreateUserRequest request) {
        // 将Request转换为User实体
        User user = userMapper.toEntity(request);
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
    public User updateUser(Integer userId, UpdateUserRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));

        // 使用Mapper更新实体（仅更新非null字段）
        userMapper.updateEntityFromRequest(request, existingUser);

        return userRepository.save(existingUser);
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

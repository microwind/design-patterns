package com.github.microwind.springboot4ddd.application.service.user;

import com.github.microwind.springboot4ddd.application.dto.user.CreateUserRequest;
import com.github.microwind.springboot4ddd.application.dto.user.UpdateUserRequest;
import com.github.microwind.springboot4ddd.application.dto.user.UserResponse;
import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import com.github.microwind.springboot4ddd.infrastructure.exception.BusinessException;
import com.github.microwind.springboot4ddd.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 * 处理用户相关的业务逻辑
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "userTransactionManager")
public class UserService {

    private final UserRepository userRepository;

    /**
     * 创建用户
     */
    public UserResponse createUser(CreateUserRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByName(request.getName())) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在: " + request.getName());
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(HttpStatus.CONFLICT, "邮箱已存在: " + request.getEmail());
        }

        // 创建用户领域对象
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .wechat(request.getWechat())
                .address(request.getAddress())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        // 保存用户
        User savedUser = userRepository.save(user);
        log.info("User created: id={}, name={}", savedUser.getId(), savedUser.getName());

        return toResponse(savedUser);
    }

    /**
     * 根据ID获取用户
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        return toResponse(user);
    }

    /**
     * 根据用户名获取用户
     */
    public UserResponse getUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "name", name));
        return toResponse(user);
    }

    /**
     * 获取所有用户
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户
     */
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        // 查找用户
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));

        // 如果更新邮箱，检查是否与其他用户重复
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(HttpStatus.CONFLICT, "邮箱已存在: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getWechat() != null) {
            user.setWechat(request.getWechat());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        user.setUpdatedTime(LocalDateTime.now());

        // 保存更新
        User updatedUser = userRepository.update(user);
        log.info("User updated: id={}, name={}", updatedUser.getId(), updatedUser.getName());

        return toResponse(updatedUser);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        // 检查用户是否存在
        if (!userRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("用户", "id", id);
        }

        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    /**
     * 转换为响应DTO
     */
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .wechat(user.getWechat())
                .address(user.getAddress())
                .createdAt(user.getCreatedTime())
                .updatedAt(user.getUpdatedTime())
                .build();
    }
}

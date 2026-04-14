package com.github.microwind.springboot4ddd.interfaces.controller.user;

import com.github.microwind.springboot4ddd.interfaces.vo.user.CreateUserRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.user.UpdateUserRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.user.UserResponse;
import com.github.microwind.springboot4ddd.application.service.user.UserService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 * 提供用户相关的REST API
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user: name={}", request.getName());
        UserResponse user = userService.createUser(request);
        return ApiResponse.success("用户创建成功", user);
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        log.info("Getting all users");
        List<UserResponse> users = userService.getAllUsers();
        return ApiResponse.success("获取用户列表成功", users);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public ApiResponse<Page<UserResponse>> getUsersByPage(Pageable pageable) {
        validatePageable(pageable);
        log.info("Getting users by page, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ApiResponse.success("分页查询用户成功", users);
    }

    /**
     * 根据ID获取用户（支持缓存穿透）
     * 优先从缓存获取，缓存未命中时从数据库加载并缓存
     */
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        log.info("查询用户信息，用户ID: {} - 开始缓存穿透查询", id);
        long startTime = System.currentTimeMillis();
        
        UserResponse user = userService.getUserById(id);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String message;
        if (duration < 50) { // 假设缓存命中响应时间小于50ms
            message = String.format("获取用户成功（缓存命中），耗时: %dms", duration);
            log.info("用户ID: {} 缓存命中，响应时间: {}ms", id, duration);
        } else {
            message = String.format("获取用户成功（数据库查询），耗时: %dms", duration);
            log.info("用户ID: {} 缓存未命中，从数据库加载并缓存，响应时间: {}ms", id, duration);
        }
        
        return ApiResponse.success(message, user);
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/name/{name}")
    public ApiResponse<UserResponse> getUserByName(@PathVariable String name) {
        log.info("Getting user by name: {}", name);
        UserResponse user = userService.getUserByName(name);
        return ApiResponse.success("获取用户成功", user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user: id={}", id);
        UserResponse user = userService.updateUser(id, request);
        return ApiResponse.success("用户更新成功", user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user: id={}", id);
        userService.deleteUser(id);
        return ApiResponse.success("用户删除成功", null);
    }

    /**
     * 验证分页参数
     */
    private void validatePageable(Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new IllegalArgumentException("分页参数 page 必须从 1 开始，当前值: " + pageable.getPageNumber());
        }
        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("分页参数 size 必须大于 0，当前值: " + pageable.getPageSize());
        }
    }
}

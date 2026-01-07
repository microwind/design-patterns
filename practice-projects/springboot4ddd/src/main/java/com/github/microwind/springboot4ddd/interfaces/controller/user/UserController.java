package com.github.microwind.springboot4ddd.interfaces.controller.user;

import com.github.microwind.springboot4ddd.application.dto.user.CreateUserRequest;
import com.github.microwind.springboot4ddd.application.dto.user.UpdateUserRequest;
import com.github.microwind.springboot4ddd.application.dto.user.UserResponse;
import com.github.microwind.springboot4ddd.application.service.user.UserService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ApiResponse.success("获取用户成功", user);
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
}

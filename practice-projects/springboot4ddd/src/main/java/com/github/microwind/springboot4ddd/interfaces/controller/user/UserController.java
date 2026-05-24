package com.github.microwind.springboot4ddd.interfaces.controller.user;

import com.github.microwind.springboot4ddd.application.command.user.CreateUserCommand;
import com.github.microwind.springboot4ddd.application.command.user.UpdateUserCommand;
import com.github.microwind.springboot4ddd.application.dto.user.UserDTO;
import com.github.microwind.springboot4ddd.application.service.user.UserService;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.page.PageableConverter;
import com.github.microwind.springboot4ddd.interfaces.vo.user.CreateUserRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.user.UpdateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping
    public ApiResponse<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user: name={}", request.getName());
        CreateUserCommand command = CreateUserCommand.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .wechat(request.getWechat())
                .address(request.getAddress())
                .build();
        UserDTO user = userService.createUser(command);
        return ApiResponse.success("用户创建成功", user);
    }

    @GetMapping
    public ApiResponse<List<UserDTO>> getAllUsers() {
        log.info("Getting all users");
        List<UserDTO> users = userService.getAllUsers();
        return ApiResponse.success("获取用户列表成功", users);
    }

    @GetMapping("/page")
    public ApiResponse<PageResult<UserDTO>> getUsersByPage(Pageable pageable) {
        validatePageable(pageable);
        log.info("Getting users by page, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        PageResult<UserDTO> users = userService.getAllUsers(PageableConverter.toDomain(pageable));
        return ApiResponse.success("分页查询用户成功", users);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long id) {
        log.info("查询用户信息，用户ID: {}", id);
        UserDTO user = userService.getUserById(id);
        return ApiResponse.success("获取用户成功", user);
    }

    @GetMapping("/name/{name}")
    public ApiResponse<UserDTO> getUserByName(@PathVariable String name) {
        log.info("Getting user by name: {}", name);
        UserDTO user = userService.getUserByName(name);
        return ApiResponse.success("获取用户成功", user);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user: id={}", id);
        UpdateUserCommand command = UpdateUserCommand.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .wechat(request.getWechat())
                .address(request.getAddress())
                .build();
        UserDTO user = userService.updateUser(id, command);
        return ApiResponse.success("用户更新成功", user);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user: id={}", id);
        userService.deleteUser(id);
        return ApiResponse.success("用户删除成功", null);
    }

    private void validatePageable(Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new IllegalArgumentException("分页参数 page 必须从 1 开始，当前值: " + pageable.getPageNumber());
        }
        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("分页参数 size 必须大于 0，当前值: " + pageable.getPageSize());
        }
    }
}

package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.user.User;
import com.microwind.knife.application.dto.user.UserPageDTO;
import com.microwind.knife.application.services.UserService;
import com.microwind.knife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 创建用户
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 根据用户ID查询
    @GetMapping("/{userId}")
    public ApiResponse<Optional<User>> getUser(@PathVariable Integer userId) {
        // return userService.getUserById(userId);
        // 或自定义ApiResponse返回
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            return ApiResponse.success(user, "查询用户成功。");
        } else {
            return ApiResponse.failure(HttpStatus.NOT_FOUND.value(), "查询用户失败。");
        }
    }

    // 更新接口
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    // POST删除接口，返回JSON
    @PostMapping("/{userId}")
    public ApiResponse<Void> removeUser(@PathVariable Integer userId) {
        try {
            userService.deleteUser(userId);
            return ApiResponse.success(null, "删除用户 " + userId + " 成功。");
        } catch (ResourceNotFoundException ex) {
            return ApiResponse.failure(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "删除用户失败：" + ex.getMessage());
        }
    }

    // DELETE删除接口，返回no_content
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    // 查询全部用户接口
    @GetMapping("")
    public UserPageDTO getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPages = userService.getAllUsers(PageRequest.of(page, size));
        return new UserPageDTO(userPages);
    }
}

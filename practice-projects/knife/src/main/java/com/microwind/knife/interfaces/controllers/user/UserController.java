package com.microwind.knife.interfaces.controllers.user;

import com.microwind.knife.application.dto.user.UserPageDTO;
import com.microwind.knife.application.services.user.UserService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.user.User;
import com.microwind.knife.exception.ResourceNotFoundException;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
import com.microwind.knife.interfaces.annotation.RequireSign;
import com.microwind.knife.interfaces.annotation.WithParams;
import com.microwind.knife.interfaces.vo.sign.SignHeaderRequest;
import com.microwind.knife.interfaces.vo.user.CreateUserRequest;
import com.microwind.knife.interfaces.vo.user.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@RequireSign  // 类级别：所有方法都需要签名验证
public class UserController {
    private final UserService userService;

    // 创建用户
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest request) {
        // 签名验证由拦截器完成，这里直接处理业务逻辑
        return userService.createUser(request);
    }

    // 根据用户ID查询
    @GetMapping("/{userId}")
    @IgnoreSignHeader // 这里设置为不需要验证签名
    public ApiResponse<User> getUser(
            @ModelAttribute("SignHeaders") SignHeaderRequest signHeaders,
            @PathVariable Integer userId) {
        if (signHeaders != null) {
            // 签名验证由拦截器完成，这里直接处理业务逻辑
            log.info("完整headers：Sign-appCode={}, Sign-sign={}, Sign-time={}, Sign-path={}",
                    signHeaders.getAppCode(), signHeaders.getSign(), signHeaders.getTime(), signHeaders.getPath());
        }
        // 或自定义ApiResponse返回
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> ApiResponse.success(u, "查询用户成功。"))
                .orElseGet(() -> ApiResponse.failure(HttpStatus.NOT_FOUND.value(), "查询用户失败。未找到userId: " + userId));
    }

    // 更新接口
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(
            @ModelAttribute("SignHeaders") SignHeaderRequest signHeaders,
            @PathVariable Integer userId,
            @RequestBody UpdateUserRequest request) {
        // 签名验证由拦截器完成，这里直接处理业务逻辑
        User user = userService.updateUser(userId, request);
        return ApiResponse.success(user, "更新用户成功。");
    }

    // 删除接口，返回JSON
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeUser(
            @ModelAttribute("SignHeaders") SignHeaderRequest headers,
            @PathVariable Integer userId) {
        // 签名验证由拦截器完成，这里直接处理业务逻辑
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
    @DeleteMapping("/delete/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @ModelAttribute("SignHeaders") SignHeaderRequest headers,
            @PathVariable Integer userId) {
        // 签名验证由拦截器完成，这里直接处理业务逻辑
        userService.deleteUser(userId);
    }

    // 查询全部用户接口
    @GetMapping("")
    @IgnoreSignHeader
    public ApiResponse<UserPageDTO> getAllUsers(
            @ModelAttribute("SignHeaders") SignHeaderRequest headers,
            Pageable pageable) {
        // 签名验证由拦截器完成，这里直接处理业务逻辑
        Page<User> userPages = userService.getAllUsers(pageable);
        return ApiResponse.success(new UserPageDTO(userPages), "查询成功。");
    }
}

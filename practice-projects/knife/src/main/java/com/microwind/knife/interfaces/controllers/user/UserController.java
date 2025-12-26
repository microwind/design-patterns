package com.microwind.knife.interfaces.controllers.user;

import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.dto.user.UserPageDTO;
import com.microwind.knife.application.services.user.UserService;
import com.microwind.knife.application.services.sign.SignValidationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.user.User;
import com.microwind.knife.exception.ResourceNotFoundException;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
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
public class UserController {
    private final UserService userService;
    private final SignValidationService signValidationService;
    private final SignMapper signMapper;

    // 创建用户
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    // 根据用户ID查询
    @GetMapping("/{userId}")
    @IgnoreSignHeader
    public ApiResponse<User> getUser(
            @ModelAttribute("signHeaders") SignHeaderRequest signHeaders,
            @PathVariable Integer userId) {
        // return userService.getUserById(userId);
        log.info("完整headers：appCode={}, sign={}, time={}, path={}",
                signHeaders.getAppCode(), signHeaders.getSign(), signHeaders.getTime(), signHeaders.getPath());
        SignDTO signDTO = signMapper.toDTO(signHeaders);
        boolean isValid = signValidationService.validate(signDTO);
        if (!isValid) {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "签名" + signHeaders + "校验失败。");
        }
        // 或自定义ApiResponse返回
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> ApiResponse.success(u, "查询用户成功。"))
                .orElseGet(() -> ApiResponse.failure(HttpStatus.NOT_FOUND.value(), "查询用户失败。"));

    }

    // 更新接口
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    // 删除接口，返回JSON
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> removeUser(
            @ModelAttribute("signHeaders") SignHeaderRequest headers,
            @PathVariable Integer userId) {
        SignDTO signDTO = signMapper.toDTO(headers);
        boolean isValid = signValidationService.validate(signDTO);
        if (!isValid) {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "签名" + signDTO + "校验失败。");
        }
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
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }

    // 查询全部用户接口
    @GetMapping("")
    public UserPageDTO getAllUsers(Pageable pageable) {
        Page<User> userPages = userService.getAllUsers(pageable);
        return new UserPageDTO(userPages);
    }
}

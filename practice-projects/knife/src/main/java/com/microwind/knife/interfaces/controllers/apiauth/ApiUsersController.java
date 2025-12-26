package com.microwind.knife.interfaces.controllers.apiauth;

import com.microwind.knife.application.dto.apiauth.ApiUserDTO;
import com.microwind.knife.application.dto.apiauth.ApiUserMapper;
import com.microwind.knife.application.services.apiauth.ApiUsersService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.apiauth.ApiUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API用户管理Controller
 */
@RestController
@RequestMapping("/api/apiauth/users")
@RequiredArgsConstructor
public class ApiUsersController {

    private final ApiUserMapper apiUserMapper;
    private final ApiUsersService apiUsersService;

    /**
     * 验证用户
     */
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateUser(@RequestParam String appCode) {
        boolean isValid = apiUsersService.validateUser(appCode);
        return ApiResponse.success(isValid, "用户验证完成");
    }

    /**
     * 创建API用户
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApiUserDTO> createApiUser(@RequestBody ApiUsers apiUsers) {
        return ApiResponse.success(apiUsersService.createApiUser(apiUsers), "API用户创建成功");
    }

    /**
     * 获取所有用户
     */
    @GetMapping
    public ApiResponse<List<ApiUserDTO>> getAllUsers() {
        return ApiResponse.success(apiUsersService.getAllUsers(), "查询成功");
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ApiResponse<ApiUserDTO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(apiUsersService.getById(id), "查询成功");
    }

    /**
     * 根据appCode获取用户
     */
    @GetMapping("/code/{appCode}")
    public ApiResponse<ApiUserDTO> getUserByAppCode(@PathVariable String appCode) {
        return ApiResponse.success(apiUsersService.getByAppCode(appCode), "查询成功");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ApiResponse<ApiUserDTO> updateApiUser(@PathVariable Long id, @RequestBody ApiUsers apiUsers) {
        return ApiResponse.success(apiUsersService.updateApiUser(id, apiUsers), "用户信息更新成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApiUser(@PathVariable Long id) {
        apiUsersService.deleteApiUser(id);
    }
}

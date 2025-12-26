package com.microwind.knife.interfaces.controllers.apiauth;

import com.microwind.knife.application.services.apiauth.ApiAuthService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.apiauth.ApiAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API权限管理Controller
 */
@RestController
@RequestMapping("/api/apiauth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final ApiAuthService apiAuthService;

    /**
     * 检查权限
     */
    @GetMapping("/check")
    public ApiResponse<Boolean> checkAuth(@RequestParam String appCode, @RequestParam String apiPath) {
        boolean hasAuth = apiAuthService.checkAuth(appCode, apiPath);
        return ApiResponse.success(hasAuth, "权限检查完成");
    }

    /**
     * 创建权限
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApiAuth> createAuth(@RequestBody ApiAuth apiAuth) {
        return ApiResponse.success(apiAuthService.createAuth(apiAuth), "权限创建成功");
    }

    /**
     * 获取所有权限
     */
    @GetMapping
    public ApiResponse<List<ApiAuth>> getAllAuths() {
        return ApiResponse.success(apiAuthService.getAllAuths(), "查询成功");
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    public ApiResponse<ApiAuth> getAuthById(@PathVariable Long id) {
        return ApiResponse.success(apiAuthService.getById(id), "查询成功");
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public ApiResponse<ApiAuth> updateAuth(@PathVariable Long id, @RequestBody ApiAuth apiAuth) {
        return ApiResponse.success(apiAuthService.updateAuth(id, apiAuth), "权限更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuth(@PathVariable Long id) {
        apiAuthService.deleteAuth(id);
    }
}

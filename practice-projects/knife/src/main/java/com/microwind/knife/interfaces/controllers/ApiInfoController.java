package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.apiauth.ApiInfoService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.apiauth.ApiInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API信息管理Controller
 */
@RestController
@RequestMapping("/api/apiauth/info")
@RequiredArgsConstructor
public class ApiInfoController {

    private final ApiInfoService apiInfoService;

    /**
     * 创建API信息
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ApiInfo> createApiInfo(@RequestBody ApiInfo apiInfo) {
        return ApiResponse.success(apiInfoService.createApiInfo(apiInfo), "API信息创建成功");
    }

    /**
     * 获取所有API信息
     */
    @GetMapping
    public ApiResponse<List<ApiInfo>> getAllApiInfos() {
        return ApiResponse.success(apiInfoService.getAllApiInfos(), "查询成功");
    }

    /**
     * 根据ID获取API信息
     */
    @GetMapping("/{id}")
    public ApiResponse<ApiInfo> getApiInfoById(@PathVariable Long id) {
        return ApiResponse.success(apiInfoService.getById(id), "查询成功");
    }

    /**
     * 根据apiPath获取API信息
     */
    @GetMapping("/path/{apiPath}")
    public ApiResponse<ApiInfo> getApiInfoByPath(@PathVariable String apiPath) {
        return ApiResponse.success(apiInfoService.getByApiPath(apiPath), "查询成功");
    }

    /**
     * 获取需要签名的API列表
     */
    @GetMapping("/apiauth-required")
    public ApiResponse<List<ApiInfo>> getSignRequiredApis() {
        return ApiResponse.success(apiInfoService.getSignRequiredApis(), "查询成功");
    }

    /**
     * 更新API信息
     */
    @PutMapping("/{id}")
    public ApiResponse<ApiInfo> updateApiInfo(@PathVariable Long id, @RequestBody ApiInfo apiInfo) {
        return ApiResponse.success(apiInfoService.updateApiInfo(id, apiInfo), "API信息更新成功");
    }

    /**
     * 删除API信息
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApiInfo(@PathVariable Long id) {
        apiInfoService.deleteApiInfo(id);
    }
}

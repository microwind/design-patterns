package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.sign.SignValidationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.order.Order;
import com.microwind.knife.interfaces.vo.EmptyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignValidationService signValidationService;

    @GetMapping
    public ApiResponse<EmptyResponse> adminHome() {
        return ApiResponse.success(new EmptyResponse(),"Welcome to Admin");
    }

    // 携带sign访问admin路径的测试
    @RequestMapping(
            value = "/admin-sign-submit",
            method = {RequestMethod.GET, RequestMethod.POST}
    )

    public ApiResponse<Object> signSubmit(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "time", required = false) Long time,
            @RequestBody Object obj) {

        String path = "/api/admin/admin-sign-submit";

        try {
            // 执行权限、时效和签名校验
            signValidationService.validateRequest(appCode, path, sign, time);

            // 校验通过，执行业务逻辑
            log.info("签名验证，处理内容: {}", obj);
            return ApiResponse.success(obj, "sign：" + sign + "校验成功。");

        } catch (SecurityException e) {
            log.error("签名验证失败: {}", e.getMessage());
            return ApiResponse.failure(HttpStatus.FORBIDDEN.value(), "验证失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("处理请求失败: {}", e.getMessage());
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),"处理失败: " + e.getMessage());
        }
    }
}

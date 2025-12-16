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
    public ResponseEntity<String> signSubmit(
            @RequestHeader(value = "X-App-Key", required = false) String appKey,
            @RequestHeader(value = "X-Sign", required = false) String sign,
            @RequestHeader(value = "X-Sign-Time", required = false) Long signTime,
            @RequestBody Order order) {

        String path = "/api/admin/admin-sign-submit";

        try {
            // 执行权限、时效和签名校验
            signValidationService.validateRequest(appKey, path, sign, signTime);

            // 校验通过，执行业务逻辑
            log.info("签名验证通过，处理订单: {}", order);
            return ResponseEntity.ok("提交成功 - 订单号: " + order.getOrderNo() +
                    ", 金额: " + order.getAmount() +
                    ", 订单名: " + order.getOrderName());

        } catch (SecurityException e) {
            log.error("签名验证失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("验证失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("处理请求失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("处理失败: " + e.getMessage());
        }
    }
}

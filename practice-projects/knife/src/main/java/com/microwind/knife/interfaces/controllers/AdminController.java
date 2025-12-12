package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.sign.SignValidationService;
import com.microwind.knife.domain.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignValidationService signValidationService;

    @GetMapping
    public String adminHome() {
        return "Welcome to the Admin Dashboard";
    }

    // 携带sign访问admin路径的测试
    @PostMapping("/admin-apiauth-submit")
    public ResponseEntity<String> signSubmit(
            @RequestHeader("X-App-Key") String appKey,
            @RequestHeader("X-Sign") String sign,
            @RequestHeader("X-Sign-Time") Long signTime,
            @RequestBody Order order) {

        String path = "/admin/admin-apiauth-submit";

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

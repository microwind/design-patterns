package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.domain.sign.DynamicSalt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/sign/page")
@RequiredArgsConstructor
public class SignPageController {

    private final DynamicSaltService dynamicSaltService;

    // 签名测试页面
    @GetMapping("/sign-test")
    public String signTest() {
        return "sign-test";
    }

    // 签名页面 - 下发动态盐值
    @GetMapping("/sign-page")
    public String demoPage(Model model) {
        // 为/admin/admin-apiauth-submit接口生成动态盐值
        String appCode = "apiauth-caller1";
        String targetPath = "/api/admin/admin-sign-submit";

        try {
            DynamicSalt dynamicSalt = dynamicSaltService.generate(appCode, targetPath);
            // 将动态盐值信息传递给前端页面
            model.addAttribute("appCode", appCode);
            model.addAttribute("path", dynamicSalt.path());
            model.addAttribute("dynamicSalt", dynamicSalt.saltValue());
            model.addAttribute("dynamicSaltTime", dynamicSalt.generateTime());
        } catch (IllegalArgumentException e) {
            log.error("签名验证失败: {}", e.getMessage());
        }
        return "demo-page";
    }
}

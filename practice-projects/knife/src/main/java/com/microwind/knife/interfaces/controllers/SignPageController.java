package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.domain.sign.DynamicSalt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String signPage(Model model) {
        // 为/admin/admin-sign-submit接口生成动态盐值
        String appKey = "sign-caller1";
        String targetPath = "/admin/admin-sign-submit";

        DynamicSalt dynamicSalt = dynamicSaltService.generate(appKey, targetPath);

        // 将动态盐值信息传递给前端页面
        model.addAttribute("appKey", appKey);
        model.addAttribute("path", dynamicSalt.getPath());
        model.addAttribute("dynamicSalt", dynamicSalt.getSaltValue());
        model.addAttribute("dynamicSaltTime", dynamicSalt.getGenerateTime());

        return "sign-page";
    }
}

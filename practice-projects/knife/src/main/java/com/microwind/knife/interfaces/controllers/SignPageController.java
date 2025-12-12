package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.domain.sign.DynamicSalt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/apiauth/page")
@RequiredArgsConstructor
public class SignPageController {

    private final DynamicSaltService dynamicSaltService;

    // 签名测试页面
    @GetMapping("/apiauth-test")
    public String signTest() {
        return "apiauth-test";
    }

    // 签名页面 - 下发动态盐值
    @GetMapping("/apiauth-page")
    public String signPage(Model model) {
        // 为/admin/admin-apiauth-submit接口生成动态盐值
        String appKey = "apiauth-caller1";
        String targetPath = "/admin/admin-apiauth-submit";

        DynamicSalt dynamicSalt = dynamicSaltService.generate(appKey, targetPath);

        // 将动态盐值信息传递给前端页面
        model.addAttribute("appKey", appKey);
        model.addAttribute("path", dynamicSalt.path());
        model.addAttribute("dynamicSalt", dynamicSalt.saltValue());
        model.addAttribute("dynamicSaltTime", dynamicSalt.generateTime());

        return "apiauth-page";
    }
}

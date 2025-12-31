package com.microwind.knife.interfaces.controllers.sign;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
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
        // 为接口生成动态盐值
        String appCode = "ios1";
        String path = "/api/admin/admin-sign-submit";

        try {
            // 注意 DynamicSaltService.generate 返回 DynamicSaltDTO
            DynamicSaltDTO dynamicSaltDTO = dynamicSaltService.generate(appCode, path);
            // 将动态盐值信息传递给前端页面
            model.addAttribute("appCode", appCode);
            model.addAttribute("path", dynamicSaltDTO.getApiPath());
            model.addAttribute("dynamicSalt", dynamicSaltDTO.getDynamicSalt());
            model.addAttribute("dynamicSaltTime", dynamicSaltDTO.getSaltTimestamp());
            log.info("为页面生成动态盐值: appCode={}, path={}, dynamicSalt={}, time={}",
                appCode, path, dynamicSaltDTO.getDynamicSalt(), dynamicSaltDTO.getSaltTimestamp());
        } catch (IllegalArgumentException e) {
            log.error("动态盐值生成失败: {}", e.getMessage());
            // 如果失败，设置默认值
            model.addAttribute("appCode", appCode);
            model.addAttribute("path", path);
            model.addAttribute("dynamicSalt", "");
            model.addAttribute("dynamicSaltTime", 0L);
        }
        return "demo-page";
    }
}

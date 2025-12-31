package com.microwind.knife.interfaces.controllers.admin;

import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
import com.microwind.knife.interfaces.annotation.RequireSign;
import com.microwind.knife.interfaces.annotation.WithParams;
import com.microwind.knife.interfaces.vo.EmptyResponse;
import com.microwind.knife.interfaces.vo.sign.SignHeaderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireSign(withParams = WithParams.TRUE)   // 类级别：所有方法都需要签名验证且指定参数校验
public class AdminController {

    @GetMapping
    @IgnoreSignHeader  // 公开接口，不需要签名验证
    public ApiResponse<EmptyResponse> adminHome() {
        return ApiResponse.success(new EmptyResponse(), "Welcome to Admin");
    }

    @RequestMapping(
            value = "/admin-sign-submit",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResponse<Object> adminSignSubmit(
            @ModelAttribute("SignHeaders") SignHeaderRequest headers,
            @RequestBody(required = false) Map<String, Object> params) {

        // 签名验证由拦截器完成，这里直接处理业务逻辑
        log.info("header 参数: {}", headers);
        log.info("签名验证通过，接收参数: {}", params);
        return ApiResponse.success(params, "sign：" + headers.getSign() + " 校验成功。");
    }
}

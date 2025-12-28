package com.microwind.knife.interfaces.controllers.admin;

import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.sign.SignValidationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.interfaces.vo.EmptyResponse;
import com.microwind.knife.interfaces.vo.sign.SignHeaderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SignValidationService signValidationService;
    private final SignMapper signMapper;

    @GetMapping
    public ApiResponse<EmptyResponse> adminHome() {
        return ApiResponse.success(new EmptyResponse(),"Welcome to Admin");
    }

    @RequestMapping(
            value = "/admin-sign-submit",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResponse<Object> adminSignSubmit(
            @ModelAttribute("SignHeaders") SignHeaderRequest headers,
            @RequestHeader(value = "Sign-withParams", required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> params) {

            String sign = headers.getSign();

            // 执行权限、时效和签名校验
            SignDTO signDTO = signMapper.toDTO(headers);
            boolean isValid;
            // 根据 header 中的 withParams 显式指定是否携带参数
            if (Boolean.TRUE.equals(withParams)) {
                isValid = signValidationService.validateWithParams(signDTO, params);
            } else {
                isValid = signValidationService.validate(signDTO);
            }

            // 校验通过，执行业务逻辑
            if (isValid) {
                log.info("签名验证，接收参数: {}", params);
                return ApiResponse.success(params, "sign：" + sign + "校验成功。");
            } else {
                log.error("签名验证失败: {}", params);
                return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "sign：" + sign + "验证失败。");
            }
    }
}

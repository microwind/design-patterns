package com.microwind.knife.interfaces.controllers;
import com.microwind.knife.application.dto.sign.*;
import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.application.services.sign.SignService;
import com.microwind.knife.application.services.sign.SignVerifyService;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.Sign;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sign")
@RequiredArgsConstructor
public class SignController {
    private final DynamicSaltService dynamicSaltService;
    private final SignService signService;
    private final SignVerifyService signVerifyService;

    // 动态盐值生成接口
    @PostMapping("/dynamic-salt")
    public ResponseEntity<DynamicSaltResponseDTO> generateDynamicSalt(@RequestBody DynamicSaltRequestDTO request) {
        DynamicSalt salt = dynamicSaltService.generate(request.getAppKey(), request.getPath());
        DynamicSaltResponseDTO response = new DynamicSaltResponseDTO();
        response.setPath(salt.getPath());
        response.setDynamicSalt(salt.getSaltValue());
        response.setDynamicSaltTime(salt.getGenerateTime());
        return ResponseEntity.ok(response);
    }

    // 签名生成接口
    @PostMapping("/sign-generate")
    public ResponseEntity<SignResponseDTO> generateSign(@RequestBody SignRequestDTO request) {
        Sign sign = signService.generate(
                request.getAppKey(),
                request.getPath(),
                request.getDynamicSalt(),
                request.getDynamicSaltTime()
        );
        SignResponseDTO response = new SignResponseDTO();
        response.setPath(sign.getPath());
        response.setSign(sign.getSignValue());
        response.setTime(sign.getTimestamp());
        return ResponseEntity.ok(response);
    }

    // 测试提交接口，其他保存接口可参考
    @PostMapping("/submit-test")
    public ResponseEntity<Map<String, String>> submit(@RequestBody SignVerifyRequestDTO request) {
        String result = signVerifyService.submit(
                request.getAppKey(),
                request.getPath(),
                request.getSign(),
                request.getTime(),
                request.getData()
        );
        Map<String, String> response = new HashMap<>();
        response.put("result", result);
        return ResponseEntity.ok(response);
    }
}
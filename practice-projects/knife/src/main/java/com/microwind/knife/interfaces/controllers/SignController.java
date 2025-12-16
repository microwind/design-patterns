package com.microwind.knife.interfaces.controllers;
import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.application.services.sign.SignService;
import com.microwind.knife.application.services.sign.SignVerifyService;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.interfaces.vo.sign.SignVerifyRequest;
import com.microwind.knife.interfaces.vo.sign.SignRequest;
import com.microwind.knife.interfaces.vo.sign.DynamicSaltResponse;
import com.microwind.knife.interfaces.vo.sign.SignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
    private final DynamicSaltService dynamicSaltService;
    private final SignService signService;
    private final SignVerifyService signVerifyService;

    // 动态盐值生成接口
    @PostMapping("/dynamic-salt-generate")
    public ResponseEntity<DynamicSaltResponse> generateDynamicSalt(@RequestBody SignVerifyRequest request) {
        DynamicSalt salt = dynamicSaltService.generate(request.getAppKey(), request.getPath());
        DynamicSaltResponse response = new DynamicSaltResponse();
        response.setPath(salt.path());
        response.setDynamicSalt(salt.saltValue());
        response.setDynamicSaltTime(salt.generateTime());
        return ResponseEntity.ok(response);
    }

    // 签名生成接口
    @PostMapping("/generate")
    public ResponseEntity<SignResponse> generateSign(@RequestBody SignRequest request) {
        Sign sign = signService.generate(
                request.getAppKey(),
                request.getPath(),
                request.getDynamicSalt(),
                request.getDynamicSaltTime()
        );
        SignResponse response = new SignResponse();
        response.setPath(sign.path());
        response.setSign(sign.signValue());
        response.setTime(sign.timestamp());
        return ResponseEntity.ok(response);
    }

    // 测试提交接口，其他保存接口可参考
    @PostMapping("/submit-test")
    public ResponseEntity<Map<String, String>> submit(@RequestBody SignVerifyRequest request) {
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
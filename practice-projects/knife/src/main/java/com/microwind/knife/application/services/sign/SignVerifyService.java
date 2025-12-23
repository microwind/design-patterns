package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.domain.repository.ApiAuthRepository;
import com.microwind.knife.domain.sign.SignUserAuth;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignVerifyService {
    private final SignDomainService securityDomainService;
    private final ApiAuthRepository apiAuthRepository;
    private final SignConfig signConfig;
    private final Map<String, String> interfaceSaltMap;

    // 提交数据（含签名校验）
    public String submit(String appCode, String path, String signValue, Long signTimestamp, Map<String, Object> data) {
        // 1. 校验appCode权限
        SignUserAuth appAuth = apiAuthRepository.findByAppCode(appCode)
                .orElseThrow(() -> new IllegalArgumentException("无效的appCode：" + appCode));
        // 2. 校验提交接口权限
        if (!appAuth.permitPaths().contains(path)) {
            throw new SecurityException("appCode无提交接口权限");
        }
        // 3. 校验签名时效性
        Long ttl = signConfig.getSignatureTtl();
        if ((System.currentTimeMillis() - signTimestamp) > ttl) {
            throw new IllegalArgumentException("签名已超过有效期：" + signTimestamp);
        }

        // 4. 校验签名
        SignDTO signDTO = new SignDTO();
        signDTO.setAppCode(appCode);
        signDTO.setApiPath(path);
        signDTO.setTimestamp(signTimestamp);
        signDTO.setSignValue(signValue);
        if (!securityDomainService.validateSign(signDTO, appAuth.secretKey())) {
            throw new SecurityException("签名无效");
        }
        // 5. 执行业务逻辑（示例：保存数据）
        return "提交成功：" + data;
    }
}
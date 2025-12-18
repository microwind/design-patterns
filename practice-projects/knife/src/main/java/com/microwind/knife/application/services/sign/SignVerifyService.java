package com.microwind.knife.application.services.sign;

import com.microwind.knife.domain.repository.ApiAuthRepository;
import com.microwind.knife.domain.sign.ApiAuth;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignVerifyService {
    private final SignDomainService securityDomainService;
    private final ApiAuthRepository apiAuthRepository;
    private final Map<String, String> interfaceSaltMap;
    private static final long TIMESTAMP_VALID_DURATION = 10 * 60 * 1000;

    // 提交数据（含签名校验）
    public String submit(String appCode, String path, String signValue, Long signTimestamp, Map<String, Object> data) {
        // 1. 校验appCode权限
        ApiAuth appAuth = apiAuthRepository.findByAppCode(appCode)
                .orElseThrow(() -> new IllegalArgumentException("无效的appCode：" + appCode));
        // 2. 校验提交接口权限
        if (!appAuth.getPermitPaths().contains(path)) {
            throw new SecurityException("appCode无提交接口权限");
        }
        // 3. 校验签名时效性
        if (System.currentTimeMillis() - signTimestamp > TIMESTAMP_VALID_DURATION) {
            throw new SecurityException("签名已过期");
        }
        // 4. 校验签名
        String interfaceSalt = interfaceSaltMap.get(path);
        if (!securityDomainService.validateSign(appCode, path, appAuth.getAppSecret(), interfaceSalt, signValue, signTimestamp)) {
            throw new SecurityException("签名无效");
        }
        // 5. 执行业务逻辑（示例：保存数据）
        return "提交成功：" + data;
    }
}
package com.microwind.knife.application.services.sign;
import com.microwind.knife.application.config.ApiAuthConfig;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DynamicSaltService {
    private final SignDomainService signDomainService;
    private final ApiAuthConfig apiAuthConfig;

    // 生成动态盐值
    public DynamicSalt generate(String appCode, String path) {
        String interfaceSalt = apiAuthConfig.getInterfaceSalt(path);
        if (interfaceSalt == null) {
            throw new IllegalArgumentException("接口路径不存在：" + path);
        }
        return signDomainService.generateDynamicSalt(appCode, path, interfaceSalt);
    }
}
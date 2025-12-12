package com.microwind.knife.config;
import com.microwind.knife.domain.sign.ApiAuth;
import com.microwind.knife.domain.repository.AppAuthRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class SignPathConfig {
    // 接口专属盐值配置（从数据库中获取）
    @Bean
    public Map<String, String> interfaceSaltMap() {
        Map<String, String> saltMap = new HashMap<>();
        saltMap.put("/apiauth/submit-test", "submit_test_salt"); // 提交接口盐值
        return saltMap;
    }

    // 应用权限配置（应来自数据库或缓存）
    @Bean
    public AppAuthRepository appAuthRepository() {
        // 允许访问的接口和禁止访问的接口
        ApiAuth app001Auth = new ApiAuth(
                "APP001",
                "app001_secret_abc321",
                List.of("/apiauth/apiauth-generate", "/apiauth/submit-test"),
                List.of()
        );
        Map<String, ApiAuth> authMap = new HashMap<>();
        authMap.put("APP001", app001Auth);

        return appKey -> Optional.ofNullable(authMap.get(appKey));
    }
}